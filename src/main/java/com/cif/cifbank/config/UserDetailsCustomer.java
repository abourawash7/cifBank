package com.cif.cifbank.config;

import com.cif.cifbank.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserDetailsCustomer implements UserDetailsService {
    private final CustomerRepository customerRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var customerInfo =customerRepository.findByEmailIgnoreCase(username).orElseThrow(() ->
                new UsernameNotFoundException("User details not found  for the user or user Blocked: " + username));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(customerInfo.getRole()));
        return  new User(customerInfo.getEmail(), customerInfo.getPassword(), authorities);
    }
}
