/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.nerfo.decodificador.vo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Fernando
 */
public class NodoVO {

    private String nameState;
    private Integer distHamming;
    private String valueCod;
    private NodoVO next0;
    private NodoVO next1;

    public NodoVO() {
    }

    public NodoVO(String nameState, Integer distHamming, String valueCod, NodoVO next0, NodoVO next1) {
        this.nameState = nameState;
        this.distHamming = distHamming;
        this.valueCod = valueCod;
        this.next0 = next0;
        this.next1 = next1;
    }

    public String getNameState() {
        return nameState;
    }

    public void setNameState(String nameState) {
        this.nameState = nameState;
    }

    public Integer getDistHamming() {
        return distHamming;
    }

    public void setDistHamming(Integer distHamming) {
        this.distHamming = distHamming;
    }

    public String getValueCod() {
        return valueCod;
    }

    public void setValueCod(String valueCod) {
        this.valueCod = valueCod;
    }

    public NodoVO getNext0() {
        return next0;
    }

    public void setNext0(NodoVO next0) {
        this.next0 = next0;
    }

    public NodoVO getNext1() {
        return next1;
    }

    public void setNext1(NodoVO next1) {
        this.next1 = next1;
    }

    /**
     * Metodo encargado de contar los nodos hoja.
     *
     * @param raiz
     * @param count
     */
    public static void contNodosHoja(NodoVO raiz, AtomicInteger count) {
        if (raiz != null) {
            if (raiz.getNext0() == null && raiz.getNext1() == null) {
                count.getAndIncrement();
            } else {
                contNodosHoja(raiz.getNext0(), count);
                contNodosHoja(raiz.getNext1(), count);
            }
        }
    }

    /**
     * Metodo encargado de eliminar nodos hoja que superan la distancia de
     * hamming permitida para el sistema.
     *
     * @param raiz
     * @param maxDistHamming
     * @return
     */
    public static NodoVO deleteNodoHoja(NodoVO raiz, Integer maxDistHamming) {
        if (raiz != null) {
            if (isHoja(raiz.getNext0())) {
                if (raiz.getNext0().getDistHamming() > maxDistHamming) {
                    raiz.setNext0(null);
                }
            } else {
                raiz.setNext0(deleteNodoHoja(raiz.getNext0(), maxDistHamming));
            }
            if (isHoja(raiz.getNext1())) {
                if (raiz.getNext1().getDistHamming() > maxDistHamming) {
                    raiz.setNext1(null);
                }
            } else {
                raiz.setNext1(deleteNodoHoja(raiz.getNext1(), maxDistHamming));
            }
        }
        return raiz;
    }

    /**
     * Metodo que elimina nodos hoja que tienen la misma distancia de hamming,
     * sin embargo deja uno de estos nodos para posterior uso y/o evaluacion.
     *
     * @param delete
     * @param raiz
     * @param maxDistHamming
     * @return
     */
    public static NodoVO deleteNodoHojaIguales(Boolean delete, NodoVO raiz, Integer maxDistHamming) {
        Boolean deleteAct = delete;
        if (raiz != null) {
            if (isHoja(raiz.getNext0())) {
                if (raiz.getNext0().getDistHamming().equals(maxDistHamming)) {
                    if (deleteAct) {
                        raiz.setNext0(null);
                    } else {
                        deleteAct = Boolean.TRUE;
                    }
                }
            } else {
                raiz.setNext0(deleteNodoHojaIguales(deleteAct, raiz.getNext0(), maxDistHamming));
            }
            if (isHoja(raiz.getNext1())) {
                if (raiz.getNext1().getDistHamming() > maxDistHamming) {
                    if (deleteAct) {
                        raiz.setNext1(null);
                    }
                }
            } else {
                raiz.setNext1(deleteNodoHojaIguales(deleteAct, raiz.getNext1(), maxDistHamming));
            }
        }
        return raiz;
    }

    public static Boolean isHoja(NodoVO raiz) {
        if (raiz != null) {
            return raiz.getNext0() == null && raiz.getNext1() == null;
        } else {
            return false;
        }
    }
}
