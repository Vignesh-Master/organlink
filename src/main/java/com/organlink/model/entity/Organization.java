package com.organlink.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Entity representing an organization in the policy management system
 * Organizations can vote on and propose policies for organ donation
 */
@Entity
@Table(name = "organizations",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "org_id"),
           @UniqueConstraint(columnNames = "name"),
           @UniqueConstraint(columnNames = "username")
       },
       indexes = {
           @Index(name = "idx_org_id", columnList = "org_id"),
           @Index(name = "idx_org_username", columnList = "username"),
           @Index(name = "idx_org_active", columnList = "is_active"),
           @Index(name = "idx_org_location", columnList = "location")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"password"})
@EqualsAndHashCode(callSuper = true, exclude = {"password"})
public class Organization extends BaseEntity {

    @NotBlank(message = "Organization ID is required")
    @Size(min = 3, max = 50, message = "Organization ID must be between 3 and 50 characters")
    @Column(name = "org_id", nullable = false, length = 50, unique = true)
    private String orgId;

    @NotBlank(message = "Organization name is required")
    @Size(min = 2, max = 200, message = "Organization name must be between 2 and 200 characters")
    @Column(name = "name", nullable = false, length = 200, unique = true)
    private String name;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    @Column(name = "location", length = 100)
    private String location;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be at least 6 characters")
    @Column(name = "password", nullable = false, length = 255)
    @JsonIgnore
    private String password;

    @Column(name = "can_vote", nullable = false)
    private Boolean canVote = true;

    @Column(name = "can_propose", nullable = false)
    private Boolean canPropose = true;

    @Column(name = "register_blockchain", nullable = false)
    private Boolean registerBlockchain = false;

    @Size(max = 66, message = "Transaction hash cannot exceed 66 characters")
    @Column(name = "transaction_hash", length = 66)
    private String transactionHash;

    @Size(max = 42, message = "Blockchain address cannot exceed 42 characters")
    @Column(name = "blockchain_address", length = 42)
    private String blockchainAddress;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Constructors
    public Organization(String orgId, String name, String location, String username, String password) {
        this.orgId = orgId;
        this.name = name;
        this.location = location;
        this.username = username;
        this.password = password;
    }

    // Business logic methods
    public boolean canParticipateInVoting() {
        return isActive && canVote;
    }

    public boolean canCreateProposals() {
        return isActive && canPropose;
    }

    public boolean isBlockchainEnabled() {
        return registerBlockchain && transactionHash != null;
    }

    public void enableBlockchain(String transactionHash, String blockchainAddress) {
        this.registerBlockchain = true;
        this.transactionHash = transactionHash;
        this.blockchainAddress = blockchainAddress;
    }

    public void disableBlockchain() {
        this.registerBlockchain = false;
        this.transactionHash = null;
        this.blockchainAddress = null;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    // Helper method for display
    public String getDisplayName() {
        return name + " (" + orgId + ")";
    }
}
