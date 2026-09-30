package com.example.data.local

object PredefinedAccounts {

    val RAHBAR = FarmerEntity(
        id = 1,
        username = "abdurahmon",
        password = "rahbar",
        role = "RAHBAR",
        dehqonId = "IFX-LEADER-000",
        name = "Abdumalikov Abdurahmon",
        landSizeHectares = 8.9, // Jami dehqonlar yer maydoni yig'indisi
        annualPlanTargetUzs = 89_000_000L, // Jami yillik reja yig'indisi
        phone = "+998 90 345 67 89",
        cropType = "Bug'doy va Paxta (Bosh xo'jalik)",
        region = "Ittifoq MFY, Markaziy bino"
    )

    // Foydalanuvchi taqdim etgan 11 nafar dehqonlar ro'yxati va to'lov rejalari
    val DEHQONS = listOf(
        FarmerEntity(
            id = 2,
            username = "abduvosiq",
            password = "kabinet1",
            role = "DEHQON",
            dehqonId = "IFX-2026-001",
            name = "Abduvosiq",
            landSizeHectares = 2.0,
            annualPlanTargetUzs = 20_000_000L,
            phone = "+998 90 111 22 33",
            cropType = "G'alla va sabzavot",
            region = "Ittifoq MFY, 1-hudud"
        ),
        FarmerEntity(
            id = 3,
            username = "saidkarim",
            password = "kabinet2",
            role = "DEHQON",
            dehqonId = "IFX-2026-002",
            name = "Saidkarim",
            landSizeHectares = 1.0,
            annualPlanTargetUzs = 10_000_000L,
            phone = "+998 91 222 33 44",
            cropType = "Paxtachilik",
            region = "Ittifoq MFY, 2-hudud"
        ),
        FarmerEntity(
            id = 4,
            username = "sobit",
            password = "kabinet3",
            role = "DEHQON",
            dehqonId = "IFX-2026-003",
            name = "Sobit",
            landSizeHectares = 1.0,
            annualPlanTargetUzs = 10_000_000L,
            phone = "+998 93 333 44 55",
            cropType = "G'allachilik",
            region = "Ittifoq MFY, 1-hudud"
        ),
        FarmerEntity(
            id = 5,
            username = "xolmurod",
            password = "kabinet8",
            role = "DEHQON",
            dehqonId = "IFX-2026-004",
            name = "Xolmurod",
            landSizeHectares = 1.0,
            annualPlanTargetUzs = 10_000_000L,
            phone = "+998 99 888 99 00",
            cropType = "Poliz ekinlari",
            region = "Ittifoq MFY, 3-hudud"
        ),
        FarmerEntity(
            id = 6,
            username = "karim",
            password = "kabinet4",
            role = "DEHQON",
            dehqonId = "IFX-2026-005",
            name = "Karim",
            landSizeHectares = 0.7,
            annualPlanTargetUzs = 7_000_000L,
            phone = "+998 94 444 55 66",
            cropType = "Sabzavot ekinlari",
            region = "Ittifoq MFY, 3-hudud"
        ),
        FarmerEntity(
            id = 7,
            username = "ravshan",
            password = "kabinet6",
            role = "DEHQON",
            dehqonId = "IFX-2026-006",
            name = "Ravshan",
            landSizeHectares = 0.6,
            annualPlanTargetUzs = 6_000_000L,
            phone = "+998 97 666 77 88",
            cropType = "Paxtachilik",
            region = "Ittifoq MFY, 4-hudud"
        ),
        FarmerEntity(
            id = 8,
            username = "mirzohid",
            password = "kabinet5",
            role = "DEHQON",
            dehqonId = "IFX-2026-007",
            name = "Mirzohid",
            landSizeHectares = 0.5,
            annualPlanTargetUzs = 5_000_000L,
            phone = "+998 95 555 66 77",
            cropType = "Beda va g'alla",
            region = "Ittifoq MFY, 2-hudud"
        ),
        FarmerEntity(
            id = 9,
            username = "abdujabbor",
            password = "kabinet7",
            role = "DEHQON",
            dehqonId = "IFX-2026-008",
            name = "Abdujabbor",
            landSizeHectares = 0.4,
            annualPlanTargetUzs = 4_000_000L,
            phone = "+998 98 777 88 99",
            cropType = "Makkajo'xori",
            region = "Ittifoq MFY, 1-hudud"
        ),
        FarmerEntity(
            id = 10,
            username = "hamidulla",
            password = "kabinet9",
            role = "DEHQON",
            dehqonId = "IFX-2026-009",
            name = "Hamidulla aka",
            landSizeHectares = 0.3,
            annualPlanTargetUzs = 3_000_000L,
            phone = "+998 90 999 00 11",
            cropType = "Kungaboqar",
            region = "Ittifoq MFY, 2-hudud"
        ),
        FarmerEntity(
            id = 11,
            username = "ubaydulla",
            password = "kabinet10",
            role = "DEHQON",
            dehqonId = "IFX-2026-010",
            name = "Ubaydulla",
            landSizeHectares = 0.2,
            annualPlanTargetUzs = 2_000_000L,
            phone = "+998 91 123 78 90",
            cropType = "Sabzavot",
            region = "Ittifoq MFY, 4-hudud"
        ),
        FarmerEntity(
            id = 12,
            username = "shuhrat",
            password = "kabinet11",
            role = "DEHQON",
            dehqonId = "IFX-2026-011",
            name = "Shuhrat",
            landSizeHectares = 0.2,
            annualPlanTargetUzs = 2_000_000L,
            phone = "+998 93 234 89 01",
            cropType = "Kartoshka",
            region = "Ittifoq MFY, 3-hudud"
        )
    )

    val ALL_ACCOUNTS: List<FarmerEntity> = listOf(RAHBAR) + DEHQONS

    fun findMatching(username: String, password: String): FarmerEntity? {
        val cleanUser = username.trim().lowercase()
        val cleanPass = password.trim()
        return ALL_ACCOUNTS.firstOrNull {
            it.username.lowercase() == cleanUser && it.password == cleanPass
        }
    }

    /**
     * Oylik reja (12 oy) summasini hisoblash
     */
    fun getMonthlyPlanUzs(annualTarget: Long): Long {
        return when (annualTarget) {
            20_000_000L -> 1_666_667L
            10_000_000L -> 833_333L
            7_000_000L -> 583_333L
            6_000_000L -> 500_000L
            5_000_000L -> 416_667L
            4_000_000L -> 333_333L
            3_000_000L -> 250_000L
            2_000_000L -> 166_667L
            else -> annualTarget / 12L
        }
    }
}
