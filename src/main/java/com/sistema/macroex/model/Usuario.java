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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @Size(max = 13, message = "No máximo 10 número")
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

}
