package com.sistema.macroex.service;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import com.sistema.macroex.model.Role;
import com.sistema.macroex.model.Usuario;
import com.sistema.macroex.repository.RoleRepository;
import com.sistema.macroex.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
@Transactional
public class SSUserDetailsService implements UserDetailsService {

    private UsuarioRepository usuarioRepository;

    public SSUserDetailsService(UsuarioRepository userRepository){
        this.usuarioRepository = userRepository;
    }

    @Autowired
    RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        try {
            Usuario user = usuarioRepository.findByUsuarioByLogin(email);
            if(user==null || !user.getEnable()){
                throw new UsernameNotFoundException("Usuario não encontrado!");
            }

            user.setSenha(new BCryptPasswordEncoder().encode(user.getSenha()));

            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getSenha(), getAuthories(user));
        }

        catch (Exception e)
        {
         throw new UsernameNotFoundException("Usuario não encontrado!");
        }

    }

    // método para substituir a implementação da classe usuario das atribuir ROLES
    private Set<GrantedAuthority> getAuthories(Usuario user){


        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role: user.getRoles()){
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getName());
            authorities.add(grantedAuthority);
        }
        return authorities;
    }
    
}
