package airbnbb11.entities;

import airbnbb11.entities.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_gen")
    @SequenceGenerator(name = "user_gen", sequenceName = "user_seq", allocationSize = 1, initialValue = 21)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String image;
    private String stripeId;
    private LocalDate createdDate;
    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> messagesFromAdmin;
    @Enumerated(EnumType.STRING)
    private Role role;
    private boolean enabled;
    private boolean isAccountNonLocked;
    private int failedLoginAttempts;
    private Date lockTime;
    @OneToMany(cascade = {
            CascadeType.REMOVE,
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE},
            mappedBy = "user")
    private List<House> houses;

    @OneToMany(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.REMOVE},
            mappedBy = "user")
    private List<Favourite> favorites;

    @OneToMany(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.REMOVE},
            mappedBy = "user")
    private List<Booking> bookings;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
