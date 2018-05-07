/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.nerfo.decodificador;

import co.com.nerfo.decodificador.vo.MachineStatesVO;
import co.com.nerfo.decodificador.vo.NodoVO;
import co.com.nerfo.decodificador.vo.StatesVO;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;

/**
 *
 * @author Fernando
 */
public class DecoManager {

    private static final Logger logger = Logger.getLogger(DecoManager.class);

    private static final Gson gson = new Gson();

    private static String INIT_STATE = "S0";
    private static Integer ERROR_PERMITIDO = 3;

    /**
     * Metodo administrativo del servicio web encargado de decodificar un
     * mensaje.
     *
     * @param machineJson
     * @param wordCod
     * @return
     */
    public String decodificarPalabra(String machineJson, String wordCod) {
        String response = "";
        try {
            MachineStatesVO machine = gson.fromJson(machineJson, MachineStatesVO.class);
            List<String> codWordList = getListCodWord(machine, wordCod);
            NodoVO arbol = algorithmViterbi(machine, codWordList);
            // Se ejecuta proceso de decodificacion
            response = decodeViterbi(arbol);
            response = validateBinary(response);
            response = convertBinaryStringToString(response);
        } catch (Exception ex) {
            logger.error("Error durante la decodificacion del mensaje: " + ex.getMessage(), ex);
        }
        return response;
    }

    /**
     * Metodo principal del algoritmo de viterbi, este se encarga de construir
     * un arbol con todos los posibles caminos que se pueden tomar para
     * completar la secuencia de bits a decodificar, adicional a ello, contiene
     * metodos de borrado con el fin de no dejar el arbol tan grande, esto con
     * el fin de optimizar la busqueda del mejor camino.
     *
     * @param machine
     * @param codWord
     * @return
     */
    public NodoVO algorithmViterbi(MachineStatesVO machine, List<String> codWord) {
        NodoVO response = new NodoVO();
        response.setNameState(INIT_STATE);
        response.setDistHamming(0);
        AtomicInteger countHojas = new AtomicInteger(0);
        for (String codRec : codWord) {
            response = algorithmViterbi(response, machine, codRec);
            NodoVO.contNodosHoja(response, countHojas);
            logger.info("[algorithmViterbi] Hojas: " + countHojas);
            Integer maxDistHamming = ERROR_PERMITIDO;
            if (countHojas.get() > 4) {
                response = NodoVO.deleteNodoHoja(response, maxDistHamming);
                countHojas.set(0);
                NodoVO.contNodosHoja(response, countHojas);
                if (countHojas.get() > 4) {
                    response = NodoVO.deleteNodoHojaIguales(Boolean.FALSE, response, maxDistHamming);
                }
                countHojas.set(0);
                NodoVO.contNodosHoja(response, countHojas);
            }
            countHojas.set(0);
            NodoVO.contNodosHoja(response, countHojas);
            logger.info("[algorithmViterbi] Nuevo Conteo Hojas: " + countHojas);
            countHojas.set(0);
        }
        return response;
    }

    /**
     * Metodo auxiliar para el algoritmo de viterbi, este metodo permite
     * construir el diagrama de trellis, sin embargo, el metodo almacena todos
     * los posibles caminos que permite generar el algoritmo para completar la
     * secuencia de bits decodificados.
     *
     * @param nodo
     * @param machine
     * @param codPos
     * @return
     */
    private NodoVO algorithmViterbi(NodoVO nodo, MachineStatesVO machine, String codPos) {
        StatesVO stInicial = machine.getStateVO(nodo.getNameState());
        // Evaluacion con Entrada 0
        if (nodo.getNext0() == null) {
            String cod0 = stInicial.salidasToString(0);
            NodoVO nodoAux = new NodoVO();
            nodoAux.setNameState(machine.getNameState(stInicial.nextValues(0)));
            nodoAux.setDistHamming(nodo.getDistHamming() + pesoHamming(cod0, codPos));
            nodoAux.setValueCod("0");
            // Se agrega nodo hijo
            nodo.setNext0(nodoAux);
        } else {
            nodo.setNext0(algorithmViterbi(nodo.getNext0(), machine, codPos));
        }
        // Evaluacion con Entrada 1
        if (nodo.getNext1() == null) {
            String cod1 = stInicial.salidasToString(1);
            NodoVO nodoAux = new NodoVO();
            nodoAux.setNameState(machine.getNameState(stInicial.nextValues(1)));
            nodoAux.setDistHamming(nodo.getDistHamming() + pesoHamming(cod1, codPos));
            nodoAux.setValueCod("1");
            // Se agrega nodo hijo
            nodo.setNext1(nodoAux);
        } else {
            nodo.setNext1(algorithmViterbi(nodo.getNext1(), machine, codPos));
        }
        return nodo;
    }

    /**
     * Metodo que calcula el peso o la distancia de hamming de acuerdo al
     * segmento codificado y al valor real codificado.
     *
     * @param codRec
     * @param codPos
     * @return
     */
    private Integer pesoHamming(String codRec, String codPos) {
        Integer peso = 0;
        char[] arrayCodRec = codRec.toCharArray();
        char[] arrayCodPos = codPos.toCharArray();
        for (int i = 0; i < arrayCodRec.length; i++) {
            if (arrayCodRec[i] != arrayCodPos[i]) {
                peso++;
            }
        }
        return peso;
    }

    /**
     * Metodo encargado de convertir la palabra codificada en una lista de
     * elementos codificados segun las salidas de la maquina de estados.
     *
     * @param machine
     * @param secCodificada
     * @return
     */
    public static List<String> getListCodWord(MachineStatesVO machine, String secCodificada) {
        Integer numOut = machine.getOperaciones().size();
        List<String> response = new ArrayList<>();
        Integer indexMin = 0;
        Integer indexMax = numOut;
        while (indexMax <= secCodificada.length()) {
            response.add(secCodificada.substring(indexMin, indexMax));
            indexMin += numOut;
            indexMax += numOut;
        }
        return response;
    }

    /**
     * Metodo encargado de construir la palabra decodificada.
     *
     * @param arbol
     * @return
     * @throws java.lang.Exception
     */
    public String decodeViterbi(NodoVO arbol) throws Exception {
        String response = "";
        while (arbol.getNext0() != null && arbol.getNext1() != null) {
            // Evaluacion si los pesos de hamming de los dos nodo hoja son iguales.
            if (arbol.getNext0().getDistHamming().equals(arbol.getNext1().getDistHamming())) {
                List<Integer> pesos = new ArrayList<>();
                Integer minor1 = arbol.getNext0().getNext0() != null ? arbol.getNext0().getNext0().getDistHamming() : 100;
                pesos.add(minor1);
                Integer minor2 = arbol.getNext0().getNext1() != null ? arbol.getNext0().getNext1().getDistHamming() : 100;
                pesos.add(minor2);
                Integer minor3 = arbol.getNext1().getNext0() != null ? arbol.getNext1().getNext0().getDistHamming() : 100;
                pesos.add(minor3);
                Integer minor4 = arbol.getNext1().getNext1() != null ? arbol.getNext1().getNext1().getDistHamming() : 100;
                pesos.add(minor4);
                Integer minor = getMinorValue(pesos);
                if (minor.equals(minor1) || minor.equals(minor2)) {
                    response += arbol.getNext0().getValueCod();
                    arbol = arbol.getNext0();
                } else if (minor.equals(minor3) || minor.equals(minor4)) {
                    response += arbol.getNext1().getValueCod();
                    arbol = arbol.getNext1();
                }
            } // Evaluacion de la distancia de hamming si es mayor la rama izquierda con respecto a la rama derecha
            else if (arbol.getNext0().getDistHamming() < arbol.getNext1().getDistHamming()) {
                response += arbol.getNext0().getValueCod();
                arbol = arbol.getNext0();
            } else {
                response += arbol.getNext1().getValueCod();
                arbol = arbol.getNext1();
            }
        }
        return response;
    }

    /**
     * Metodo que evalua dentro de una lista de pesos o distancia de hamming
     * cual es el menor.
     *
     * @param pesos
     * @return
     */
    private Integer getMinorValue(List<Integer> pesos) {
        Integer minor = pesos.get(0);
        for (Integer peso : pesos) {
            if (peso < minor) {
                minor = peso;
            }
        }
        return minor;
    }

    /**
     * Metodo encargado de validar los 8 bits de un caracter de una palabra.
     *
     * @param binario
     * @return
     */
    public static String validateBinary(String binario) {
        if (binario.length() == 8) {
            return binario;
        } else {
            Integer ceros = 8 - binario.length();
            for (int i = 0; i < ceros; i++) {
                binario = "0" + binario;
            }
            return binario;
        }
    }

    /**
     * Metodo encargado de convertir un codigo binario a su respectiva palabra
     * en ascii.
     *
     * @param binario
     * @return
     */
    public static String convertBinaryStringToString(String binario) {
        StringBuilder sb = new StringBuilder();
        char[] chars = binario.replaceAll("\\s", "").toCharArray();
        int[] mapping = {1, 2, 4, 8, 16, 32, 64, 128};

        for (int j = 0; j < chars.length; j += 8) {
            int idx = 0;
            int sum = 0;
            for (int i = 7; i >= 0; i--) {
                if (chars[i + j] == '1') {
                    sum += mapping[idx];
                }
                idx++;
            }
            sb.append(Character.toChars(sum));
        }
        return sb.toString();
    }
}
