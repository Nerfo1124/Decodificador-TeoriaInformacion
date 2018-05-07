/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.nerfo.decodificador.ws;

/**
 *
 * @author Fernando
 */
public interface IDecodificadorService {
    
    String decodeMessage(String machineStates, String msgCod) throws Exception;
}
