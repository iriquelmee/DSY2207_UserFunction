package com.grupo8;

import com.grupo8.dao.UsuarioDao;
import com.grupo8.models.LoginReq;
import com.grupo8.models.Usuario;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

public class Function {

    private final UsuarioDao usuarioDAO = new UsuarioDao();

    @FunctionName("insertUsuario")
    public HttpResponseMessage insertUsuario(@HttpTrigger(name = "req", methods = {HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<Usuario>> request, ExecutionContext context) {

        context.getLogger().info("Procesando insercion de usuario ....");
        Optional<Usuario> usuarioOpt = request.getBody();

        if (!usuarioOpt.isPresent()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("El cuerpo de la solicitud está vacío o mal formado").build();
        }

        Usuario usuario = usuarioOpt.get();

        try {
            usuarioDAO.insertar(usuario);
            return request.createResponseBuilder(HttpStatus.OK).body("Usuario insertado correctamente.").build();
        } 
        catch (Exception e) {
            context.getLogger().severe("Error al insertar el usuario: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al insertar el usuario en la base de datos").build();
        }
    }

    @FunctionName("actualizarUsuario")
    public HttpResponseMessage actualizarUsuario(@HttpTrigger(name = "req", methods = {HttpMethod.PUT }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<Usuario>> request, ExecutionContext context) {

        context.getLogger().info("Procesando actualizacion de usuario ....");
        Optional<Usuario> usuarioOpt = request.getBody();

        if (!usuarioOpt.isPresent()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("El cuerpo de la solicitud está vacío o mal formado").build();
        }

        Usuario usuario = usuarioOpt.get();

        try {
            usuarioDAO.actualizar(usuario);
            return request.createResponseBuilder(HttpStatus.OK).body("Usuario actualizado correctamente").build();
        } 
        catch (Exception e) {
            context.getLogger().severe("Error al actualizar el usuario: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el usuario en la base de datos").build();
        }
    }

    @FunctionName("eliminarUsuario")
    public HttpResponseMessage eliminarUsuario(@HttpTrigger(name = "req", methods = {HttpMethod.DELETE }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request, ExecutionContext context) {

        context.getLogger().info("Procesando eliminacion de usuario ....");
        Optional<String> idOpt = request.getBody();

        if (!idOpt.isPresent()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("El id del usuario es obligatorio").build();
        }

        String id = idOpt.get();

        try {
            usuarioDAO.eliminar(id);
            return request.createResponseBuilder(HttpStatus.OK).body("Usuario eliminado correctamente").build();
        } 
        catch (Exception e) {
            context.getLogger().severe("Error al eliminar el usuario: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el usuario en la base de datos").build();
        }
    }

    @FunctionName("obtenerUsuarios")
    public HttpResponseMessage obtenerUsuarios(@HttpTrigger(name = "req", methods = {HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request, ExecutionContext context) {

        context.getLogger().info("get todos los usuarios ....");
        try 
        {
            var usuarios = usuarioDAO.obtenerTodos();
            if (usuarios.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("No se encontraron usuarios").build();
            }
            return request.createResponseBuilder(HttpStatus.OK).body(usuarios).build();
            
        } 
        catch (Exception e) {
            context.getLogger().severe("Error al obtener los usuarios: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener los usuarios de la base de datos").build();
        }
    }

    @FunctionName("buscarUsuarioPorRut")
    public HttpResponseMessage buscarUsuarioPorRut(@HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request, ExecutionContext context) {

        context.getLogger().info("Procesando búsqueda de usuario por rut...");

        String rut = request.getHeaders().get("rut");

        if (rut == null || rut.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("campo header es requisito").build();
        }

        UsuarioDao usuarioDao = new UsuarioDao();
        
        try {
            Optional<Usuario> usuario = usuarioDao.buscarPorRut(rut);

            if (usuario != null) {
                return request.createResponseBuilder(HttpStatus.OK).body(usuario).build();
            } 
            else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND).body("Usuario no encontrado para el rut: " + rut).build();
            }
        } catch (Exception e) {
            context.getLogger().severe("Error en la base de datos: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar el usuario.").build();
        }
    }

    @FunctionName("loginUsuario")
    public HttpResponseMessage loginUsuario(@HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<LoginReq>> request, ExecutionContext context) {

        context.getLogger().info("Procesando login de usuario...");

        Optional<LoginReq> loginReqOpt = request.getBody();

        if (!loginReqOpt.isPresent() || loginReqOpt.get().getNickname() == null || loginReqOpt.get().getPass() == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("{\"status\": \"error\", \"message\": \"Se requieren el nickname y la contraseña.\"}").build();
        }

        LoginReq loginReq = loginReqOpt.get();

        try {
            String idUsuario = usuarioDAO.validarCredenciales(loginReq.getNickname(), loginReq.getPass());

            if (!idUsuario.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.OK).body("{\"status\": \"success\", \"message\": \"Inicio de sesion exitoso.\", \"ID_USUARIO\": \"" + idUsuario + "\"}").build();
            } 
            else {
                return request.createResponseBuilder(HttpStatus.UNAUTHORIZED).body("{\"status\": \"error\", \"message\": \"Credenciales incorrectas.\"}").build();
            }
            
        } 
        catch (Exception e) {
            context.getLogger().severe("Error en la base de datos: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\": \"error\", \"message\": \"Error al procesar la solicitud.\"}").build();
        }
    }


}
