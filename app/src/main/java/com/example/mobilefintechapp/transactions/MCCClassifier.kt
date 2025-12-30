package com.example.mobilefintechapp.transactions

object MCCClassifier {

    // Halal MCC codes
    private val halalMCCs = setOf(
        "5411", // Grocery Stores, Supermarkets
        "5912", // Drug Stores and Pharmacies
        "5541", // Service Stations (Gas)
        "5812", // Eating Places, Restaurants (General)
        "5814", // Fast Food Restaurants (Halal certified)
        "5331", // Variety Stores
        "5499", // Miscellaneous Food Stores
        "5621", // Women's Clothing Stores
        "5651", // Family Clothing Stores
        "5732", // Electronics Stores
        "5942", // Book Stores
        "5211", // Lumber, Building Materials Stores
        "5999", // Miscellaneous Retail Stores
        "7230", // Beauty and Barber Shops
        "7298", // Health and Beauty Spas
        "4111", // Local Commuter Transport
        "4121", // Taxicabs, Limousines
        "5511", // Car and Truck Dealers
        "5200", // Home Supply Warehouse Stores
        "8011", // Doctors, Physicians
        "8021", // Dentists, Orthodontists
        "8099", // Medical Services
        "8220", // Colleges, Universities
        "8211", // Elementary, Secondary Schools
        "4814", // Telecommunication Services
        "5999", // Convenience Stores (Non-alcohol)
        "5661", // Shoe Stores
        "5712", // Furniture Stores
        "5813", // Drinking Places (Non-alcoholic)
        "7011", // Hotels, Motels
        "7996", // Amusement Parks
        "5945", // Hobby, Toy, Game Shops
        "5941", // Sporting Goods Stores
        "5655", // Sports Apparel Stores
        "5947", // Gift, Card, Novelty Stores
    )

    // Haram MCC codes
    private val haramMCCs = setOf(
        "5921", // Package Stores – Beer, Wine, Liquor
        "5993", // Cigar Stores and Stands
        "7995", // Betting/Casino Gambling
        "7801", // Government-Licensed Casinos (Online Gambling)
        "7802", // Government-Licensed Horse/Dog Racing
        "7994", // Video Game Arcades (Gambling)
        "5813", // Drinking Places (Alcoholic Beverages) – Bars, Taverns, Nightclubs
        "7273", // Dating Services
        "5735", // Record Shops (Adult content)
        "7841", // Video Tape Rental Stores (Adult content)
        "7922", // Theatrical Producers (Adult entertainment)
        "7929", // Bands, Orchestras (Nightclub entertainment)
        "5967", // Direct Marketing – Inbound Teleservices (Adult)
        "5122", // Drugs, Proprietaries, Sundries (Alcohol)
        "7993", // Video Amusement Games (Gambling machines)
        "7032", // Sporting/Recreation Camps (Casinos)
        "7299", // Miscellaneous Personal Services (Adult services)
    )

    // Merchant name keywords for additional classification
    private val haramKeywords = listOf(
        "casino", "bet", "gambling", "liquor", "wine", "beer", "bar",
        "pub", "nightclub", "lottery", "pork", "bacon", "ham", "alcohol"
    )

    private val halalKeywords = listOf(
        "halal", "islamic", "muslim", "masjid", "mosque", "mydin",
        "99 speedmart", "family mart", "guardian", "watsons"
    )

    /**
     * Classify transaction status based on MCC and merchant name
     */
    fun classifyTransaction(mcc: String, merchantName: String): TransactionStatus {
        val merchantLower = merchantName.lowercase()

        // Check merchant name keywords first (higher priority)
        if (halalKeywords.any { merchantLower.contains(it) }) {
            return TransactionStatus.HALAL
        }

        if (haramKeywords.any { merchantLower.contains(it) }) {
            return TransactionStatus.HARAM
        }

        // Check MCC codes
        return when {
            halalMCCs.contains(mcc) -> TransactionStatus.HALAL
            haramMCCs.contains(mcc) -> TransactionStatus.HARAM
            else -> TransactionStatus.UNKNOWN
        }
    }

    /**
     * Get merchant category name from MCC
     */
    fun getMerchantCategory(mcc: String): String {
        return when (mcc) {
            "5411" -> "Groceries"
            "5912" -> "Pharmacy"
            "5541" -> "Fuel"
            "5812", "5814" -> "Food"
            "5921" -> "Liquor Store"
            "5993" -> "Tobacco"
            "7995", "7801", "7802" -> "Gambling"
            "5813" -> "Beverages"
            "5999" -> "Convenience"
            "5331" -> "Variety Store"
            "5621", "5651" -> "Clothing"
            "5732" -> "Electronics"
            "5942" -> "Books"
            "7230", "7298" -> "Beauty & Personal Care"
            "4111", "4121" -> "Transportation"
            "8011", "8021", "8099" -> "Healthcare"
            "8220", "8211" -> "Education"
            "4814" -> "Telecom"
            "5712" -> "Furniture"
            "7011" -> "Accommodation"
            "7996" -> "Entertainment"
            "5941", "5945", "5655" -> "Sports & Recreation"
            else -> "Others"
        }
    }
}