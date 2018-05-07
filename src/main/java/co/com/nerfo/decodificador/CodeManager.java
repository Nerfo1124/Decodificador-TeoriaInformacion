/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.nerfo.decodificador;

import co.com.nerfo.decodificador.vo.MachineStatesVO;
import co.com.nerfo.decodificador.vo.StatesVO;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Fernando
 */
public class CodeManager {

    private static final Logger logger = Logger.getLogger(CodeManager.class);

    private static final Gson gson = new Gson();

    public String getMaquinaEstados(List<String> operaciones) {
        Integer numMemorias = 0;
        MachineStatesVO machine = new MachineStatesVO();
        for (String operacion : operaciones) {
            numMemorias = getNumMemoriasByOperation(numMemorias, operacion);
            machine.addOperacion(operacion);
        }

        Integer numEstados = (int) Math.pow(2, numMemorias);

        for (int i = 0; i < numEstados; i++) {
            if ((numMemorias - Integer.toBinaryString(i).length()) > 0) {
                String regex = "%0" + (numMemorias - Integer.toBinaryString(i).length()) + "d";
                machine.addEstado(new StatesVO("S" + i, String.format(regex, 0) + Integer.toBinaryString(i)));
            } else {
                machine.addEstado(new StatesVO("S" + i, Integer.toBinaryString(i)));
            }
        }
        
        Integer[] entrada = {0, 1};
        machine.getEstados().forEach((estado) -> {
            for (Integer in : entrada) {
                estado.getSalidas(in);
            }
        });
        
        return gson.toJson(machine);
    }

    /**
     * Metodo encargado de codificar una palabra a partir de la maquina de
     * estados.
     *
     * @param machineJson
     * @param palabra
     * @return
     */
    public List<String> codificarPalabra(String machineJson, String palabra) {
        List<String> response = new ArrayList<>();
        MachineStatesVO machine = gson.fromJson(machineJson, MachineStatesVO.class);
        String palabraBinario = wordToBinary(palabra);
        System.out.println("Palabra en Binario: " + palabraBinario);
        
        Integer numMemorias = 0;
        for (String operacion : machine.getOperaciones()) {
            numMemorias = getNumMemoriasByOperation(numMemorias, operacion);
        }

        // Creacion de un array con los bits a codificar
        char[] bin = palabraBinario.toCharArray();
        // Codificacion de la palabra
        String codificacion = "";
        String printEstados = "";
        String estInicial = String.format("%0" + (numMemorias) + "d", 0);
        for (char c : bin) {
            Integer input = Integer.parseInt("" + c);
            for (StatesVO estado : machine.getEstados()) {
                if (estado.valuesToString().equals(estInicial)) {
                    printEstados += estado.getName() + ",";
                    codificacion += estado.salidasToString(input);
                    estInicial = estado.nextValues(input);
                    break;
                }
            }
        }
        printEstados += machine.getNameState(estInicial);
        List<String> secComplete = null;
//        if (!machine.getNameState(estInicial).equals(machine.getEstados().get(0).getName())) {
//            logger.info("La secuencia de codificacion no finalizo en el estado inicial, se inicia ejecucion del metodo encargado de completar la secuencia de bits.");
//            secComplete = secuenciaTerminacion(machine, machine.getNameState(estInicial));
//        }
        response.add(palabraBinario + (secComplete != null ? secComplete.get(0) : ""));
        response.add(printEstados + (secComplete != null ? secComplete.get(1) : ""));
        response.add(codificacion + (secComplete != null ? secComplete.get(2) : ""));
        return response;
    }

    /**
     * Metodo encargado de convertir una palabra a binario.
     *
     * @param word
     * @return
     */
    public static String wordToBinary(String word) {
        String response = "";
        char[] array = word.toCharArray();
        for (char c : array) {
            response += validateBinary(Integer.toBinaryString(c));
        }
        return response;
    }

    /**
     * Metodo encargado de obtener el numero de elementos de memoria a partir de
     * las ecuaciones de la maquina de estados
     *
     * @param numMemorias
     * @param operation
     * @return
     */
    public Integer getNumMemoriasByOperation(Integer numMemorias, String operation) {
        // Busqueda del numero de memorias a utilizar segun la ecuacion de salida
        String[] objs = operation.replaceAll(" +", "").trim().toUpperCase().split("XOR");
        for (String obj : objs) {
            if (obj.contains("M")) {
                Integer aux = Integer.parseInt(obj.replace("M", ""));
                if (aux > numMemorias) {
                    numMemorias = aux;
                }
            }
        }
        return numMemorias;
    }

    /**
     * Metodo encargado de calcular la secuencia de terminacion cuando el estado
     * de finalizacion de la codificacion es diferente del estado inicial (S0)
     *
     * @param machine
     * @param finEstado
     * @return
     */
    public List<String> secuenciaTerminacion(MachineStatesVO machine, String finEstado) {
        List<String> response = new ArrayList<>();
        String iniEstado = machine.getEstados().get(0).getName();
        String secFinBits = "";
        String secFinEstados = "";
        String secFinSalidas = "";
        Boolean sec = Boolean.TRUE;
        Integer bitSecuencia = 0;
        while (sec) {
            for (int i = 0; i < machine.getEstados().size(); i++) {
                String nextState0 = "";
                String nextState1 = "";
                if (machine.getEstados().get(i).getName().equals(finEstado)) {
                    nextState0 = machine.getEstados().get(i).nextValues(0);
                    nextState1 = machine.getEstados().get(i).nextValues(1);
                    if (machine.getNameState(nextState0).equals(iniEstado)) {
                        bitSecuencia = 0;
                        finEstado = machine.getNameState(nextState0);
                        sec = Boolean.FALSE;
                    } else if (machine.getNameState(nextState1).equals(iniEstado)) {
                        bitSecuencia = 1;
                        finEstado = machine.getNameState(nextState1);
                        sec = Boolean.FALSE;
                    } else {
                        finEstado = getStateMinor(machine.getNameState(nextState0),
                                machine.getNameState(nextState1));
                        if (finEstado.equals(machine.getNameState(nextState0))) {
                            bitSecuencia = 0;
                        } else {
                            bitSecuencia = 1;
                        }
                    }
                    secFinBits += bitSecuencia;
                    secFinSalidas += machine.getEstados().get(i).salidasToString(bitSecuencia);
                }
            }
            secFinEstados += "," + finEstado;
        }
        response.add(secFinBits);
        response.add(secFinEstados);
        response.add(secFinSalidas);
        return response;
    }

    /**
     * Metodo encargado de elegir el camino hacia el estado de menor valor.
     *
     * @param st1
     * @param st2
     * @return
     */
    public static String getStateMinor(String st1, String st2) {
        Integer numSt1 = Integer.parseInt(st1.replaceAll("S", ""));
        Integer numSt2 = Integer.parseInt(st2.replaceAll("S", ""));
        if (numSt1 < numSt2) {
            return st1;
        } else {
            return st2;
        }
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
}
