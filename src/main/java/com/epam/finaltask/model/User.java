package com.epam.finaltask.model;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users", schema = "traveldb" )
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

	@Column(nullable = false, unique = true)
    private String username;

    private String password;

	@Enumerated(EnumType.STRING)
    private Role role;

	@ManyToMany
	@JoinTable(
			name = "users_vouchers",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "voucher_id")
	)
	@JsonManagedReference
	private List<Voucher> vouchers;

    private String phoneNumber;

	private String email;

    private BigDecimal balance;

    private Boolean active;

	private String avatarPath;

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = role.getAuthorities();

		authorities.addAll(role.getPermissions().stream()
				.map(permission -> new SimpleGrantedAuthority("PERMISSION_" + permission.name()))
				.toList());

		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return active != null && active;
	}
}