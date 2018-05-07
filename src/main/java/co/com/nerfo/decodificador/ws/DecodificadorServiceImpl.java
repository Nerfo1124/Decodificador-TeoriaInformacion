/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.nerfo.decodificador.ws;

import co.com.nerfo.decodificador.DecoManager;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 *
 * @author Fernando
 */
@WebService(serviceName = "DecodificadorService")
public class DecodificadorServiceImpl implements IDecodificadorService {

    private DecoManager manager;
    
    public DecodificadorServiceImpl() {
        manager = new DecoManager();
    }
    
    /**
     * Metodo encargado de realizar la decodificacion de un mensaje.
     * @param machineStates
     * @param msgCod
     * @return 
     * @throws java.lang.Exception 
     */
    @WebMethod(operationName = "decodeMessage")
    @Override
    public String decodeMessage(@WebParam(name = "machineStates") String machineStates, 
                                @WebParam(name = "msgCod") String msgCod) throws Exception {
        String response = null;
        try {
            response = manager.decodificarPalabra(machineStates, msgCod);
            return response;
        } catch (Throwable ex) {
            throw new Exception("Ocurrion un error durante la decodificacion.", ex);
        }
    }
}
