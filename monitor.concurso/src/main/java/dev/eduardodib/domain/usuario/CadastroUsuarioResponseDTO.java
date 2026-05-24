package dev.eduardodib.domain.usuario;


public record CadastroUsuarioResponseDTO(Long id, String nome, String email) {

    public static CadastroUsuarioResponseDTO fromEntity(UsuarioEntity usuario) {
        return new CadastroUsuarioResponseDTO(usuario.id, usuario.nome, usuario.email);
    }
}