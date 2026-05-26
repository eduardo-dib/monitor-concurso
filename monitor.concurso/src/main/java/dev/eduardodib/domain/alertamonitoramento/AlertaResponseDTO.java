package dev.eduardodib.domain.alertamonitoramento;



public record AlertaResponseDTO(
        Long id,
        String palavrasChave,
        String estado,
        String municipio,
        String orgao,
        boolean ativo,
        String usuarioNome,
        String usuarioEmail
) {
    public static AlertaResponseDTO fromEntity(AlertaMonitoramentoEntity alerta) {
        return new AlertaResponseDTO(
                alerta.id,
                alerta.palavrasChave,
                alerta.estado,
                alerta.municipio,
                alerta.orgao,
                alerta.ativo,
                alerta.usuario.nome,
                alerta.usuario.email
        );
    }
}
