package com.sistema.macroex.service;

import java.util.List;
import java.util.stream.Collectors;

import com.sistema.macroex.handleException.ExceptionNegotion;
import com.sistema.macroex.model.ContraRotulo;
import com.sistema.macroex.model.Perfil;
import com.sistema.macroex.model.Status;
import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.repository.RotuloRepository;
import com.sistema.macroex.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageImpl;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ContraRotuloService {

    private UsuarioRepository repository;
    /** Testando sem final */

    private RotuloRepository rotuloRepository;

    /** Puxar uma lista de Fonnecedores */
    public Page<Usuario> buscarTodosFornecedores(Pageable pageable) {
        return repository.findByPerfil(Perfil.FORNECEDOR, pageable);
    }

    /** Puxar uma lista de Fonnecedores filtrado por nome */
    public Page<Usuario> buscarFornecedoresFiltradoPorNOme(String nome, Pageable pageable) {
        return repository.findByNomeContainsIgnoreCaseAndPerfil(nome, Perfil.FORNECEDOR, pageable);
    }

    /** Descobrir quem é USuario logado (ADM ou FORNECEDOR) */
    public Usuario user() {
        return repository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /** Verificar se usuario locago é ADM */
    public Boolean isNotAdm(Usuario usuario) {
        return usuario.getPerfil() != Perfil.ADMINISTRADOR;
    }

    /** Colocar data somente na criação e quando for finalizar */
    public Boolean colocarDataQuandoCriarAndFinalizar(ContraRotulo rotulo) {
        return rotulo.getDataCriacao() == null || rotulo.getStatus().equals(Status.FINALIZADO);
    }

    /** Salvar rotulo */
    public void salvarRotulo(ContraRotulo rotulo) {
        rotuloRepository.save(rotulo);
    }

    /**
     * Filtro de uma lista pegar todos os não cadastrado os cadastrados somente o
     * Usuario poderia ver Fornecedor pdoera ver so NOTIFICADOS e FINALIZADOS
     */
    public Page<ContraRotulo> filtroDaListaExpecificaParaPage(Usuario user) {

        // Criar uma lista (se tiver) de rotulos com status diferntes de cadastrado
        List<ContraRotulo> todosFiltrado = rotuloRepository.buscaPorUsuario(user).stream()
                .filter(c -> c.getStatus() != Status.CADASTRADO).collect(Collectors.toList());

        return new PageImpl<>(todosFiltrado, PageRequest.of(0, 5, Sort.by("id")), todosFiltrado.size());
    }

    /** Lista todos os Usuarios - isto poderia ver pelo ADM (funcionários) */
    public Page<ContraRotulo> ListarTodosUsuarios() {
        return rotuloRepository.findAll(PageRequest.of(0, 5, Sort.by("id")));
    }

    /** Métodod para pegar todos rotulos para tabela */
    public Page<ContraRotulo> buscarTodosRotulos() {
        return rotuloRepository.findAll(PageRequest.of(0, 5, Sort.by("id")));
    }

    /** Métodod para pegar todos rotulos para tabela */
    public Page<ContraRotulo> buscarTodosRotulos(Pageable pageable) {
        return rotuloRepository.findAll(pageable);
    }

    /** Método para verificar se existe o rotulo */
    public ContraRotulo verificarExisteIdRotulo(Long id) {
        ContraRotulo rotulo = rotuloRepository.findById(id)
                .orElseThrow(() -> new ExceptionNegotion("Item não encontrado"));
        return rotulo;
    }

    public Page<ContraRotulo> filtroDaListaExpecificaPorTituloParaPage(Usuario user, String nome, Pageable pageable) {
        List<ContraRotulo> todosFiltrado = rotuloRepository.buscaPorUsuarioETitulo(user, nome).stream()
                .filter(rotulo -> rotulo.getStatus() != Status.CADASTRADO).collect(Collectors.toList());

        return new PageImpl<>(todosFiltrado, pageable, todosFiltrado.size());
    }

    public Page<ContraRotulo> buscarPorTitulo(String nome, Pageable pageable){
        return rotuloRepository.findByTituloContainsIgnoreCase(nome, pageable);
    }

    public Page<ContraRotulo> buscarUsuarioFiltroTitulo (Usuario user, String nome, Pageable pageable){
        return  rotuloRepository.findByUsuarioAndTituloContainsIgnoreCase(user, nome, pageable);
    }

    public Page<ContraRotulo> busscarTodosRotulosFiltroNome(String nome, Pageable pageable){
        return rotuloRepository.findByTituloContainsIgnoreCase(nome, pageable);
    }

}
