package com.sistema.macroex.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Size(max = 50,message = "Número máximo de caracteres")
    private String nome;

    @Size(max = 20)
    private String doc;

    @Size(max = 14, message = "No máximo 10 número")
    private String telefone;

    @Email(message = "Email em formato errado")
    private String email;

    private String senha;

    private String username;

    private Boolean enable; // Usuário ativo

    @Enumerated(EnumType.STRING)
    private Perfil perfil; // Perfil do usuário

    /** Fetch eager é imporante para puxar roles e acessar  */
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "roles_id"))
    private List<Role> roles;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval =true)
    private List<ContraRotulo> contrarotulo;

    @OneToMany(mappedBy = "adm", cascade = CascadeType.ALL)
    private List<ContraRotulo> criar;

    @Embedded
    private Endereco endereco;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Usuario other = (Usuario) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<ContraRotulo> getContrarotulo() {
        return contrarotulo;
    }

    public void setContrarotulo(List<ContraRotulo> contrarotulo) {
        this.contrarotulo = contrarotulo;
    }

    public List<ContraRotulo> getCriar() {
        return criar;
    }

    public void setCriar(List<ContraRotulo> criar) {
        this.criar = criar;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    

}
