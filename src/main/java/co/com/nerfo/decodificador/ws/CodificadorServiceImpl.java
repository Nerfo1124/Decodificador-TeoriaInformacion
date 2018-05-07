/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.nerfo.decodificador.ws;

import co.com.nerfo.decodificador.CodeManager;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.apache.log4j.Logger;

/**
 *
 * @author Fernando
 */
@WebService(serviceName = "CodificadorService")
public class CodificadorServiceImpl implements ICodificadorService {

    private static final Logger logger = Logger.getLogger(CodificadorServiceImpl.class);
    private static CodeManager manager;

    public CodificadorServiceImpl() {
        manager = new CodeManager();
    }

    @Override
    @WebMethod(operationName = "getMachineStates")
    public String getMachineStates(@WebParam(name = "operaciones") List<String> operaciones) throws Exception {
        String response = null;
        try {
            logger.info("============= INICIO - getMachineStates ================");
            response = manager.getMaquinaEstados(operaciones);
            logger.info("============= FIN    - getMachineStates ================");
            return response;
        } catch (Throwable ex) {
            throw new Exception("Ocurrion un error durante la decodificacion.", ex);
        }
    }

    @Override
    @WebMethod(operationName = "codeMessage")
    public String codeMessage(@WebParam(name = "machineStates") String machineStates, @WebParam(name = "msg") String msg) throws Exception {
        String response = null;
        try {
            logger.info("============= INICIO - codeMessage ================");
            List<String> listCodif = manager.codificarPalabra(machineStates, msg);
            response = listCodif.get(2);
            logger.info("============= FIN    - codeMessage ================");
            return response;
        } catch (Throwable ex) {
            throw new Exception("Ocurrion un error durante la decodificacion.", ex);
        }
    }
}
