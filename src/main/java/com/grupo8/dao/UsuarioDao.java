package com.grupo8.dao;

import com.grupo8.config.OracleConnectionUtil;
import com.grupo8.models.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    public void insertar(Usuario usuario) throws Exception {
        String sql = "INSERT INTO USUARIOS (NOMBRE, APELLIDO, NICKNAME, RUT, EMAIL, TELEFONO) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = OracleConnectionUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getNickname());
            ps.setString(4, usuario.getRut());
            ps.setString(5, usuario.getEmail());
            ps.setString(6, usuario.getTelefono());

            ps.executeUpdate();
            System.out.println("Usuario insertado correctamente.");
        } catch (SQLException e) {
            throw new Exception("Error al insertar usuario: " + e.getMessage(), e);
        }
    }

    public void actualizar(Usuario usuario) throws Exception {
        String sql = "UPDATE USUARIOS SET NOMBRE = ?, APELLIDO = ?, NICKNAME = ?, RUT = ?, EMAIL = ?, TELEFONO = ? WHERE ID_USUARIO = ?";

        try (Connection conn = OracleConnectionUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getNickname());
            ps.setString(4, usuario.getRut());
            ps.setString(5, usuario.getEmail());
            ps.setString(6, usuario.getTelefono());
            ps.setString(7, usuario.getId());

            int filasActualizadas = ps.executeUpdate();
            if (filasActualizadas == 0) {
                throw new Exception("No se encontró el usuario con ID_USUARIO: " + usuario.getId());
            }
            System.out.println("Usuario actualizado correctamente.");
        } catch (SQLException e) {
            throw new Exception("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    public void eliminar(String id) throws Exception {
        String sql = "DELETE FROM USUARIOS WHERE ID_USUARIO = ?";

        try (Connection conn = OracleConnectionUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);

            int filasEliminadas = ps.executeUpdate();
            if (filasEliminadas == 0) {
                throw new Exception("No se encontró el usuario con ID_USUARIO: " + id);
            }
            System.out.println("Usuario eliminado correctamente.");
        } catch (SQLException e) {
            throw new Exception("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }

    public List<Usuario> obtenerTodos() throws Exception {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT ID_USUARIO, NOMBRE, APELLIDO, NICKNAME, RUT, EMAIL, TELEFONO FROM USUARIOS";

        try (Connection conn = OracleConnectionUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getString("ID_USUARIO"));
                usuario.setNombre(rs.getString("NOMBRE"));
                usuario.setApellido(rs.getString("APELLIDO"));
                usuario.setNickname(rs.getString("NICKNAME"));
                usuario.setRut(rs.getString("RUT"));
                usuario.setEmail(rs.getString("EMAIL"));
                usuario.setTelefono(rs.getString("TELEFONO"));

                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener usuarios: " + e.getMessage(), e);
        }

        return usuarios;
    }

    public Usuario buscarPorRut(String rut) throws Exception {
        String sql = "SELECT * FROM USUARIOS WHERE RUT = ?";

        try (Connection conn = OracleConnectionUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rut);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getString("ID_USUARIO"));
                usuario.setNombre(rs.getString("NOMBRE"));
                usuario.setApellido(rs.getString("APELLIDO"));
                usuario.setNickname(rs.getString("NICKNAME"));
                usuario.setRut(rs.getString("RUT"));
                usuario.setEmail(rs.getString("EMAIL"));
                usuario.setTelefono(rs.getString("TELEFONO"));

                return usuario;
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new Exception("Error al buscar usuario por RUT: " + e.getMessage(), e);
        }
    }
}
