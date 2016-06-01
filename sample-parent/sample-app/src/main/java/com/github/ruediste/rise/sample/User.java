package com.github.ruediste.rise.sample;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.github.ruediste.rendersnakeXT.canvas.Glyphicon;
import com.github.ruediste.rise.api.InjectParameter;
import com.github.ruediste.rise.core.persistence.Updating;
import com.github.ruediste.rise.core.security.login.PasswordHash;
import com.github.ruediste.rise.core.security.login.PasswordHashingService;
import com.github.ruediste.rise.integration.GlyphiconIcon;
import com.github.ruediste.rise.sample.front.SampleRight;
import com.github.ruediste1.i18n.label.Labeled;
import com.github.ruediste1.i18n.label.PropertiesLabeled;

@Entity
@PropertiesLabeled
@Labeled
public class User {

    @GeneratedValue
    @Id
    private long id;

    private String name;

    @Embedded
    private PasswordHash hash;

    @ElementCollection
    private Set<SampleRight> grantedRights = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<SampleRight> getGrantedRights() {
        return grantedRights;
    }

    public void setGrantedRights(Set<SampleRight> grantedRights) {
        this.grantedRights = grantedRights;
    }

    public PasswordHash getHash() {
        return hash;
    }

    public void setHash(PasswordHash hash) {
        this.hash = hash;
    }

    @Labeled
    @GlyphiconIcon(Glyphicon.bullhorn)
    @Updating
    private void setPassword(@Labeled String newPassword, @InjectParameter PasswordHashingService service) {
        hash = service.createHash(newPassword);
    }
}
