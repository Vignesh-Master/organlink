package com.organlink.controller;

import com.organlink.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * FAQ Controller for hospital module
 * Provides frequently asked questions and answers for hospital users
 */
@RestController
@RequestMapping("/api/v1/faqs")
@RequiredArgsConstructor
@Tag(name = "FAQ Management", description = "Frequently Asked Questions for hospital users")
@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173"})
public class FAQController {

    private static final Logger logger = LoggerFactory.getLogger(FAQController.class);

    /**
     * Get all FAQs
     */
    @GetMapping
    @Operation(summary = "Get all FAQs", description = "Get all frequently asked questions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllFAQs(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId,
            @Parameter(description = "Category filter") 
            @RequestParam(required = false) String category) {

        logger.info("📚 Getting FAQs for hospital: {} (category: {})", tenantId, category);

        try {
            List<Map<String, Object>> faqs = generateFAQs();

            // Filter by category if provided
            if (category != null && !category.trim().isEmpty()) {
                faqs = faqs.stream()
                    .filter(faq -> category.equalsIgnoreCase((String) faq.get("category")))
                    .toList();
            }

            ApiResponse<List<Map<String, Object>>> response = ApiResponse.success(
                "FAQs retrieved successfully", faqs);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get FAQs: {}", e.getMessage());
            ApiResponse<List<Map<String, Object>>> response = ApiResponse.error(
                "Failed to retrieve FAQs: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get FAQ categories
     */
    @GetMapping("/categories")
    @Operation(summary = "Get FAQ categories", description = "Get all available FAQ categories")
    public ResponseEntity<ApiResponse<List<String>>> getFAQCategories() {

        logger.info("📂 Getting FAQ categories");

        try {
            List<String> categories = Arrays.asList(
                "Organ Donation",
                "Patient Registration", 
                "AI Matching",
                "Policies",
                "Technical Support",
                "Legal & Compliance"
            );

            ApiResponse<List<String>> response = ApiResponse.success(
                "FAQ categories retrieved successfully", categories);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get FAQ categories: {}", e.getMessage());
            ApiResponse<List<String>> response = ApiResponse.error(
                "Failed to retrieve FAQ categories: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Search FAQs
     */
    @GetMapping("/search")
    @Operation(summary = "Search FAQs", description = "Search FAQs by keyword")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchFAQs(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader(value = "X-Tenant-ID", required = false) String tenantId,
            @Parameter(description = "Search query") 
            @RequestParam String query) {

        logger.info("🔍 Searching FAQs for hospital: {} with query: {}", tenantId, query);

        try {
            List<Map<String, Object>> allFaqs = generateFAQs();
            String searchQuery = query.toLowerCase().trim();

            List<Map<String, Object>> matchingFaqs = allFaqs.stream()
                .filter(faq -> {
                    String question = ((String) faq.get("question")).toLowerCase();
                    String answer = ((String) faq.get("answer")).toLowerCase();
                    return question.contains(searchQuery) || answer.contains(searchQuery);
                })
                .toList();

            ApiResponse<List<Map<String, Object>>> response = ApiResponse.success(
                "FAQ search completed successfully", matchingFaqs);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to search FAQs: {}", e.getMessage());
            ApiResponse<List<Map<String, Object>>> response = ApiResponse.error(
                "Failed to search FAQs: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get FAQ by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get FAQ by ID", description = "Get specific FAQ by ID")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFAQById(
            @Parameter(description = "FAQ ID") 
            @PathVariable Long id) {

        logger.info("📖 Getting FAQ by ID: {}", id);

        try {
            List<Map<String, Object>> faqs = generateFAQs();
            Optional<Map<String, Object>> faq = faqs.stream()
                .filter(f -> Objects.equals(f.get("id"), id))
                .findFirst();

            if (faq.isEmpty()) {
                ApiResponse<Map<String, Object>> response = ApiResponse.error(
                    "FAQ not found with ID: " + id);
                return ResponseEntity.status(404).body(response);
            }

            ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "FAQ retrieved successfully", faq.get());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Failed to get FAQ by ID: {}", e.getMessage());
            ApiResponse<Map<String, Object>> response = ApiResponse.error(
                "Failed to retrieve FAQ: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Generate FAQ data (in production, this would come from database)
     */
    private List<Map<String, Object>> generateFAQs() {
        List<Map<String, Object>> faqs = new ArrayList<>();

        // Organ Donation FAQs
        faqs.add(createFAQ(1L, "Organ Donation", 
            "What are the medical criteria for organ donation?",
            "Organ donation criteria include brain death determination, medical suitability assessment, blood type compatibility, and absence of certain infectious diseases. Each organ has specific medical requirements that must be met."));

        faqs.add(createFAQ(2L, "Organ Donation",
            "How long can organs be preserved outside the body?",
            "Preservation times vary by organ: Heart (4-6 hours), Liver (12-18 hours), Kidneys (24-36 hours), Lungs (4-6 hours). Our system tracks these times automatically and prioritizes urgent cases."));

        // AI Matching FAQs
        faqs.add(createFAQ(3L, "AI Matching",
            "How does the AI organ matching system work?",
            "Our AI system matches organs based on blood type compatibility, tissue matching (HLA typing), geographic proximity, time on waiting list, medical urgency, and body size compatibility. The system prioritizes urgent cases while ensuring optimal outcomes."));

        faqs.add(createFAQ(4L, "AI Matching",
            "What factors does the AI consider for organ matching?",
            "The AI considers: Blood type compatibility (100% required), HLA tissue matching (6 antigens), geographic distance, patient urgency level, time on waiting list, body size compatibility, and active policy requirements."));

        // Patient Registration FAQs
        faqs.add(createFAQ(5L, "Patient Registration",
            "What documents are required for patient registration?",
            "Required documents include: Medical history, current medications list, insurance information, emergency contact details, and signed consent forms. Digital signature verification is performed using our OCR system."));

        faqs.add(createFAQ(6L, "Patient Registration",
            "How is patient data secured and stored?",
            "Patient data is encrypted using AES-256 encryption, stored on IPFS for decentralized security, and key information is recorded on blockchain for immutable audit trails. Only authorized hospital staff can access patient records."));

        // Policies FAQs
        faqs.add(createFAQ(7L, "Policies",
            "How do organizational policies affect organ allocation?",
            "Active policies from registered organizations influence matching criteria, priority scoring, and allocation rules. All policies are voted on democratically and implemented transparently through smart contracts."));

        faqs.add(createFAQ(8L, "Policies",
            "Can hospitals propose new allocation policies?",
            "Hospitals cannot directly propose policies, but they can request policy changes through registered organizations. Organizations can then propose and vote on new policies that affect the entire network."));

        // Technical Support FAQs
        faqs.add(createFAQ(9L, "Technical Support",
            "What should I do if the signature verification fails?",
            "If signature verification fails: 1) Ensure the signature is clear and complete, 2) Check file format (PNG, JPG supported), 3) Verify signer information is correct, 4) Contact technical support if issues persist."));

        faqs.add(createFAQ(10L, "Technical Support",
            "How do I access patient records from other hospitals?",
            "Cross-hospital access requires proper authorization and is managed through the blockchain network. Contact your system administrator to request access permissions for specific patient records."));

        // Legal & Compliance FAQs
        faqs.add(createFAQ(11L, "Legal & Compliance",
            "What are the legal requirements for organ transplantation?",
            "Legal requirements include: Proper medical certification, informed consent from donor/family, compliance with national organ allocation policies, documentation of brain death (for deceased donors), and adherence to hospital protocols."));

        faqs.add(createFAQ(12L, "Legal & Compliance",
            "How is patient privacy protected in the system?",
            "Patient privacy is protected through: End-to-end encryption, role-based access controls, audit logging of all access, HIPAA compliance measures, and blockchain-based consent management."));

        return faqs;
    }

    /**
     * Helper method to create FAQ object
     */
    private Map<String, Object> createFAQ(Long id, String category, String question, String answer) {
        Map<String, Object> faq = new HashMap<>();
        faq.put("id", id);
        faq.put("category", category);
        faq.put("question", question);
        faq.put("answer", answer);
        faq.put("helpful", true);
        faq.put("views", (int) (Math.random() * 1000) + 50);
        return faq;
    }
}
