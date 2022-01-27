package com.carrotlabs.smartcarrotframework

/**
 * The type used for Transaction Categories, their hierarchies, identfiers and id mappings.
 */
public class TransactionCategory {
    companion object {
        /**
         * Uncategorised category id : [Int]
         */
        public val UNCATEGORISED_INT_ID = 104

        /**
         * Number of supported categories
         */
        public val NUMBER_OF_CATEGORIES = 108

        /**
         * Uncategorised category idName : [String]
         */
        public val UNCATEGORISED_IDNAME:String = "not_categorized"

        /**
         * Uncategorised [TransactionCategory]
         */
        public val UNCATEGORISED: TransactionCategory = TransactionCategory(UNCATEGORISED_IDNAME)

        /**
         * @suppress
         */
        public val INCOME_CATEGORY_INT_IDS = listOf(99, 38, 41, 42, 40, 37, 39, 43)

        /**
         * Returns a [TransactionCategory] struct by its [TransactionCategory.intId].
         *
         * @param intId category int id
         *
         * @returns [TransactionCategory] if it found a category with intId, [TransactionCategory.UNCATEGORISED] otherwise.
         */
        public fun getCategory(intId: Int) : TransactionCategory {
            when (intId) {
                0 -> return UNCATEGORISED
                104 -> return UNCATEGORISED
                94 -> return TransactionCategory("cash")
                95 -> return TransactionCategory("fees_tax_charges")
                7 -> return TransactionCategory("federal_tax")
                12 -> return TransactionCategory("withholding_tax")
                2 -> return TransactionCategory("agency")
                6 -> return TransactionCategory("creditcardfees")
                8 -> return TransactionCategory("interest_payment")
                1 -> return TransactionCategory("accountfees")
                3 -> return TransactionCategory("bankfees")
                9 -> return TransactionCategory("latefees")
                5 -> return TransactionCategory("church_tax")
                11 -> return TransactionCategory("municipal_tax")
                4 -> return TransactionCategory("canton_tax")
                10 -> return TransactionCategory("military_tax")
                96 -> return TransactionCategory("health")
                14 -> return TransactionCategory("beauty")
                15 -> return TransactionCategory("dentist")
                13 -> return TransactionCategory("alternative_medicine")
                19 -> return TransactionCategory("massage")
                20 -> return TransactionCategory("pharmacy")
                17 -> return TransactionCategory("eye_doctor")
                18 -> return TransactionCategory("hairdresser")
                16 -> return TransactionCategory("doctor")
                97 -> return TransactionCategory("household")
                26 -> return TransactionCategory("office")
                24 -> return TransactionCategory("groceries")
                25 -> return TransactionCategory("household_supplies")
                21 -> return TransactionCategory("children")
                27 -> return TransactionCategory("phone_internet")
                22 -> return TransactionCategory("cleaning_services")
                28 -> return TransactionCategory("tv")
                23 -> return TransactionCategory("cloths_cleaning")
                98 -> return TransactionCategory("housing")
                29 -> return TransactionCategory("amortisation")
                31 -> return TransactionCategory("garden")
                36 -> return TransactionCategory("utilities")
                33 -> return TransactionCategory("mortgage_interest")
                30 -> return TransactionCategory("furniture")
                35 -> return TransactionCategory("rent")
                32 -> return TransactionCategory("mortgage")
                34 -> return TransactionCategory("renovation")
                99 -> return TransactionCategory("income")
                38 -> return TransactionCategory("heritage")
                41 -> return TransactionCategory("refund")
                42 -> return TransactionCategory("rental_income")
                40 -> return TransactionCategory("received_present")
                37 -> return TransactionCategory("bonus")
                39 -> return TransactionCategory("interest_income")
                43 -> return TransactionCategory("wage")
                100 -> return TransactionCategory("insurance")
                48 -> return TransactionCategory("legal_insurance")
                44 -> return TransactionCategory("building_insurance")
                47 -> return TransactionCategory("household_insurance")
                46 -> return TransactionCategory("health_insurance")
                45 -> return TransactionCategory("car_insurance")
                49 -> return TransactionCategory("life_insurance")
                101 -> return TransactionCategory("leisure_shopping")
                54 -> return TransactionCategory("electronics")
                62 -> return TransactionCategory("restaurants")
                58 -> return TransactionCategory("movies")
                64 -> return TransactionCategory("sport")
                53 -> return TransactionCategory("clothes")
                60 -> return TransactionCategory("newspapers")
                51 -> return TransactionCategory("bars")
                55 -> return TransactionCategory("fastfood")
                52 -> return TransactionCategory("books")
                63 -> return TransactionCategory("shoes")
                66 -> return TransactionCategory("wine")
                59 -> return TransactionCategory("music")
                61 -> return TransactionCategory("online_shopping")
                50 -> return TransactionCategory("art")
                56 -> return TransactionCategory("hobbies")
                57 -> return TransactionCategory("jewellery")
                65 -> return TransactionCategory("tabak")
                102 -> return TransactionCategory("misc")
                74 -> return TransactionCategory("presents")
                72 -> return TransactionCategory("personal_events")
                71 -> return TransactionCategory("memberfees")
                69 -> return TransactionCategory("education")
                70 -> return TransactionCategory("lawyers")
                68 -> return TransactionCategory("donations")
                67 -> return TransactionCategory("alimony")
                75 -> return TransactionCategory("professional_expenses")
                73 -> return TransactionCategory("pets")
                105 -> return TransactionCategory("payments")
                85 -> return TransactionCategory("check")
                86 -> return TransactionCategory("creditcard_payment")
                106 -> return TransactionCategory("save_invest")
                88 -> return TransactionCategory("invest")
                89 -> return TransactionCategory("pension")
                90 -> return TransactionCategory("save")
                87 -> return TransactionCategory("financial_advices")
                107 -> return TransactionCategory("travel")
                91 -> return TransactionCategory("airplane")
                93 -> return TransactionCategory("vacations")
                92 -> return TransactionCategory("hotel")
                103 -> return TransactionCategory("mobility")
                80 -> return TransactionCategory("gas")
                82 -> return TransactionCategory("public_transport")
                81 -> return TransactionCategory("parking")
                79 -> return TransactionCategory("car_service")
                76 -> return TransactionCategory("car_buy")
                77 -> return TransactionCategory("car_leasing")
                78 -> return TransactionCategory("car_rental")
                83 -> return TransactionCategory("taxi")
                84 -> return TransactionCategory("traffic_fine")
                108 -> return TransactionCategory("account_transfer")

                else -> return UNCATEGORISED
            }
        }

        /**
         * Returns category `IntId` by its String Name
         */
        public fun getIntId(idName: String) : Int {
            var result : Int

            when (idName) {
                "accountfees" -> result=1
                "agency"  -> result=2
                "bankfees"  -> result=3
                "canton_tax"  -> result=4
                "church_tax"  -> result=5
                "creditcardfees"  -> result=6
                "federal_tax"  -> result=7
                "interest_payment"  -> result=8
                "latefees"  -> result=9
                "military_tax"  -> result=10
                "municipal_tax"  -> result=11
                "withholding_tax"  -> result=12
                "alternative_medicine"  -> result=13
                "beauty"  -> result=14
                "dentist"  -> result=15
                "doctor"  -> result=16
                "eye_doctor"  -> result=17
                "hairdresser"  -> result=18
                "massage"  -> result=19
                "pharmacy"  -> result=20
                "children"  -> result=21
                "cleaning_services"  -> result=22
                "cloths_cleaning"  -> result=23
                "groceries"  -> result=24
                "household_supplies"  -> result=25
                "office"  -> result=26
                "phone_internet"  -> result=27
                "tv"  -> result=28
                "amortisation"  -> result=29
                "furniture"  -> result=30
                "garden"  -> result=31
                "mortgage"  -> result=32
                "mortgage_interest"  -> result=33
                "renovation"  -> result=34
                "rent"  -> result=35
                "utilities"  -> result=36
                "bonus"  -> result=37
                "heritage"  -> result=38
                "interest_income"  -> result=39
                "received_present"  -> result=40
                "refund"  -> result=41
                "rental_income"  -> result=42
                "wage"  -> result=43
                "building_insurance"  -> result=44
                "car_insurance"  -> result=45
                "health_insurance"  -> result=46
                "household_insurance"  -> result=47
                "legal_insurance"  -> result=48
                "life_insurance"  -> result=49
                "art"  -> result=50
                "bars"  -> result=51
                "books"  -> result=52
                "clothes"  -> result=53
                "electronics"  -> result=54
                "fastfood"  -> result=55
                "hobbies"  -> result=56
                "jewellery"  -> result=57
                "movies"  -> result=58
                "music"  -> result=59
                "newspapers"  -> result=60
                "online_shopping"  -> result=61
                "restaurants"  -> result=62
                "shoes"  -> result=63
                "sport"  -> result=64
                "tabak"  -> result=65
                "wine"  -> result=66
                "alimony"  -> result=67
                "donations"  -> result=68
                "education"  -> result=69
                "lawyers"  -> result=70
                "memberfees"  -> result=71
                "personal_events"  -> result=72
                "pets"  -> result=73
                "presents"  -> result=74
                "professional_expenses"  -> result=75
                "car_buy"  -> result=76
                "car_leasing"  -> result=77
                "car_rental"  -> result=78
                "car_service"  -> result=79
                "gas"  -> result=80
                "parking"  -> result=81
                "public_transport"  -> result=82
                "taxi"  -> result=83
                "traffic_fine"  -> result=84
                "check"  -> result=85
                "creditcard_payment"  -> result=86
                "financial_advices"  -> result=87
                "invest"  -> result=88
                "pension"  -> result=89
                "save"  -> result=90
                "airplane"  -> result=91
                "hotel"  -> result=92
                "vacations"  -> result=93
                "cash"  -> result=94
                "fees_tax_charges"  -> result=95
                "health"  -> result=96
                "household"  -> result=97
                "housing"  -> result=98
                "income"  -> result=99
                "insurance"  -> result=100
                "leisure_shopping"  -> result=101
                "misc"  -> result=102
                "mobility"  -> result=103
                "not_categorized"  -> result=104
                "payments"  -> result=105
                "save_invest"  -> result=106
                "travel"  -> result=107
                "account_transfer"  -> result=108
                else -> result = UNCATEGORISED_INT_ID
            }

            return result
        }

        /**
         * Returns an array of all top-level [TransactionCategory]
         */
        public fun getParentCategories() : List<TransactionCategory> {
            return listOf(
                TransactionCategory("not_categorized"),
                TransactionCategory("cash"),
                TransactionCategory("fees_tax_charges"),
                TransactionCategory("health"),
                TransactionCategory("household"),
                TransactionCategory("housing"),
                TransactionCategory("income"),
                TransactionCategory("insurance"),
                TransactionCategory("leisure_shopping"),
                TransactionCategory("misc"),
                TransactionCategory("payments"),
                TransactionCategory("save_invest"),
                TransactionCategory("travel"),
                TransactionCategory("mobility"),
                TransactionCategory("account_transfer")
            )
        }
    }

    /**
     * Category String Id
     */
    public var idName: String = UNCATEGORISED_IDNAME

    constructor()

    /**
     * Public constructor. if the category `id` is not set then category is initialised to the `UNCATEGORISED`.
     *
     * @param idName Transaction Category String id
     *
     * @return A [TransactionCategory] object.
     */
    constructor(idName:String?) {
        this.idName = idName ?: UNCATEGORISED_IDNAME
    }

    /**
     * Public constructor. if the category `id` is not set then category is initialised to the `UNCATEGORISED`.
     *
     * @param intId Transaction Category int id
     *
     * @return A [TransactionCategory] object
     */
    constructor(intId: Int?) {
        this.idName = if (intId == null) UNCATEGORISED_IDNAME else TransactionCategory.getCategory(intId).idName
    }

    /**
     * Returns category `Int Id`
     *
     * @return [TransactionCategory] IntId
     */
    public fun getIntId() : Int {
        return TransactionCategory.getIntId(idName)
    }

    /**
     * Returns the parent category of a sub-category.
     *
     * @return
     * - parent category of the sub-category
     * - [null] for a top-level category
     */
    public fun getParentCategory() : TransactionCategory? {
        when (idName) {
            "federal_tax", "withholding_tax", "agency", "creditcardfees","interest_payment","accountfees","bankfees","latefees","church_tax","municipal_tax","canton_tax","military_tax" -> return TransactionCategory("fees_tax_charges")
            "beauty", "dentist" ,"alternative_medicine","massage","pharmacy","eye_doctor","hairdresser","doctor" -> return TransactionCategory("health")
            "office","groceries","household_supplies","children","phone_internet","cleaning_services","tv","cloths_cleaning" -> return TransactionCategory("household")
            "amortisation","garden","utilities","mortgage_interest","furniture","rent","mortgage","renovation" -> return TransactionCategory("housing")
            "heritage","refund","rental_income","received_present","bonus","interest_income","wage" -> TransactionCategory("income")
            "legal_insurance","building_insurance","household_insurance","health_insurance","car_insurance","life_insurance" -> return TransactionCategory("insurance")
            "electronics","restaurants","movies","sport","clothes","newspapers","bars","fastfood","books","shoes","wine","music","online_shopping","art","hobbies","jewellery","tabak" -> return TransactionCategory("leisure_shopping")
            "presents","personal_events","memberfees","education","lawyers","donations","alimony","professional_expenses","pets" -> return TransactionCategory("misc")
            "check","creditcard_payment" -> return TransactionCategory("payments")
            "invest","pension","save","financial_advices" -> return TransactionCategory("save_invest")
            "airplane","vacations","hotel" -> return TransactionCategory("travel")
            "gas", "public_transport", "parking", "car_service", "car_buy", "car_leasing", "car_rental", "taxi", "traffic_fine" -> return TransactionCategory("mobility")
            else -> return null
        }

        return null
    }

    /**
     * Provides category sub-categories.
     *
     * @return a list of [TransactionCategory] for a top-level category, otherwise returns [null].
     */
    public fun getSubCategories() : List<TransactionCategory>? {
        when (idName) {
            UNCATEGORISED_IDNAME -> return null
            "cash" -> return emptyList()
            "fees_tax_charges" -> return listOf(TransactionCategory("federal_tax"),
                TransactionCategory("withholding_tax"),
                TransactionCategory("agency"),
                TransactionCategory("creditcardfees"),
                TransactionCategory("interest_payment"),
                TransactionCategory("accountfees"),
                TransactionCategory("bankfees"),
                TransactionCategory("latefees"),
                TransactionCategory("church_tax"),
                TransactionCategory("municipal_tax"),
                TransactionCategory("canton_tax"),
                TransactionCategory("military_tax"))
            "health" -> return listOf(TransactionCategory("beauty"),
                TransactionCategory("dentist"),
                TransactionCategory("alternative_medicine"),
                TransactionCategory("massage"),
                TransactionCategory("pharmacy"),
                TransactionCategory("eye_doctor"),
                TransactionCategory("hairdresser"),
                TransactionCategory("doctor"))
            "household" -> return listOf(TransactionCategory("office"),
                TransactionCategory("groceries"),
                TransactionCategory("household_supplies"),
                TransactionCategory("children"),
                TransactionCategory("phone_internet"),
                TransactionCategory("cleaning_services"),
                TransactionCategory("tv"),
                TransactionCategory("cloths_cleaning"))
            "housing" -> return listOf(TransactionCategory("amortisation"),
                TransactionCategory("garden"),
                TransactionCategory("utilities"),
                TransactionCategory("mortgage_interest"),
                TransactionCategory("furniture"),
                TransactionCategory("rent"),
                TransactionCategory("mortgage"),
                TransactionCategory("renovation"))
            "income" -> return listOf(TransactionCategory("heritage"),
                TransactionCategory("refund"),
                TransactionCategory("rental_income"),
                TransactionCategory("received_present"),
                TransactionCategory("bonus"),
                TransactionCategory("interest_income"),
                TransactionCategory("wage"))
            "insurance" -> return listOf(TransactionCategory("legal_insurance"),
                TransactionCategory("building_insurance"),
                TransactionCategory("household_insurance"),
                TransactionCategory("health_insurance"),
                TransactionCategory("car_insurance"),
                TransactionCategory("life_insurance"))
            "leisure_shopping" -> return listOf(TransactionCategory("electronics"),
                TransactionCategory("restaurants"),
                TransactionCategory("movies"),
                TransactionCategory("sport"),
                TransactionCategory("clothes"),
                TransactionCategory("newspapers"),
                TransactionCategory("bars"),
                TransactionCategory("fastfood"),
                TransactionCategory("books"),
                TransactionCategory("shoes"),
                TransactionCategory("wine"),
                TransactionCategory("music"),
                TransactionCategory("online_shopping"),
                TransactionCategory("art"),
                TransactionCategory("hobbies"),
                TransactionCategory("jewellery"),
                TransactionCategory("tabak"))
            "misc" -> return listOf(TransactionCategory("presents"),
                TransactionCategory("personal_events"),
                TransactionCategory("memberfees"),
                TransactionCategory("education"),
                TransactionCategory("lawyers"),
                TransactionCategory("donations"),
                TransactionCategory("alimony"),
                TransactionCategory("professional_expenses"),
                TransactionCategory("pets"))
            "payments" -> return listOf(TransactionCategory("check"),
                TransactionCategory("creditcard_payment"))
            "save_invest" -> return listOf(TransactionCategory("invest"),
                TransactionCategory("pension"),
                TransactionCategory("save"),
                TransactionCategory("financial_advices"))
            "travel" -> return listOf(TransactionCategory("airplane"),
                TransactionCategory("vacations"),
                TransactionCategory("hotel"))
            "mobility" -> return listOf(TransactionCategory("gas"),
                TransactionCategory("public_transport"),
                TransactionCategory("parking"),
                TransactionCategory("car_service"),
                TransactionCategory("car_buy"),
                TransactionCategory("car_leasing"),
                TransactionCategory("car_rental"),
                TransactionCategory("taxi"),
                TransactionCategory("traffic_fine"))
            "account_transfer" -> return emptyList()
            else -> return null
        }
    }

    /**
     * Finds whether a category is a top level category.
     *
     * @return [true] if the category is a top-level category, [false] otherwise.
     */
    public fun isParent() : Boolean {
        return idName == "cash" || idName == "account_transfer" || idName == TransactionCategory.UNCATEGORISED_IDNAME || getSubCategories() != null
    }

    /**
     *
     * Finds whether a category is a sub-category.
     *
     * @return [true] if the category is a sub-category, [false] otherwise.
     */
    public fun isSub() : Boolean {
        return !isParent()
    }

    /**
     * Prints [TransactionCategory.idName]
     */
    public fun toStringShort() : String {
        return idName
    }

    /**
    Prints a full [TransactionCategory.idName] path to the category.

    Returns: [TransactionCategory.idName] for a top-level category and "parent.idName / idName" for a sub-category.
     */
    public override fun toString() : String {
        val parentCategory = getParentCategory()
        return if (parentCategory == null) idName else parentCategory.idName + " / " + idName
    }
}