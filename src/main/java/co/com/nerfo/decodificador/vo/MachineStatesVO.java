/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.nerfo.decodificador.vo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fernando
 */
public class MachineStatesVO {
    
    private List<StatesVO> estados;
    private List<String> operaciones;

    public MachineStatesVO() {
    }

    public List<StatesVO> getEstados() {
        if(estados == null) {
            estados = new ArrayList<>();
        }
        return estados;
    }

    public List<String> getOperaciones() {
        return operaciones;
    }
    
    public void addEstado(StatesVO estado) {
        if(estados == null) {
            estados = new ArrayList<>();
        }
        estado.setOperaciones(operaciones);
        estados.add(estado);
    }
    
    public void addOperacion(String operacion) {
        if(this.operaciones == null) {
            this.operaciones = new ArrayList<>();
        }
        this.operaciones.add(operacion);
    }
    
    public String getNameState(String value) {
        String response = null;
        for (StatesVO estado : this.estados) {
            if(estado.valuesToString().equals(value)){
                response = estado.getName();
            }
        }
        return response;
    }
    
    public StatesVO getStateVO(String name) {
        StatesVO response = null;
        for (StatesVO estado : this.estados) {
            if(estado.getName().equals(name)){
                response = estado;
            }
        }
        return response;
    }
}
