/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.nerfo.decodificador.ws;

import java.util.List;

/**
 *
 * @author Fernando
 */
public interface ICodificadorService {
    
    String getMachineStates(List<String> operaciones) throws Exception;
    String codeMessage(String machineStates, String msg) throws Exception;
}
