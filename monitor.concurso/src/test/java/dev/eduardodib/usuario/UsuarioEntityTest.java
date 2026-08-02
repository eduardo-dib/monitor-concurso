package dev.eduardodib.usuario;

import dev.eduardodib.domain.usuario.UsuarioEntity;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class UsuarioEntityTest {

    @Test
    @TestTransaction
    void devePersistirEBuscarUsuarioPorEmail() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.nome = "Eduardo Teste";
        usuario.email = "teste.integracao@example.com";
        usuario.senhaHash = "hash-fake-para-teste";
        usuario.persist();

        assertNotNull(usuario.id, "O id deveria ser gerado apos o persist");

        UsuarioEntity encontrado = UsuarioEntity.findByEmail("teste.integracao@example.com");

        assertNotNull(encontrado, "Deveria encontrar o usuario pelo email");
        assertEquals(usuario.id, encontrado.id);
        assertEquals("Eduardo Teste", encontrado.nome);
    }
}
