package com.example.giapan_beta

object Categoria {
    val data: Map<String, List<String>>

        get() {
            val listaCategorieSecondarie = LinkedHashMap<String, List<String>>()

            val Piatti = listOf("Piatti Sushi", "Piatti Sashimi", "Piatti per Salse [< 13 cm]", "Piatti da Contorno [13-15 cm]", "Piatti Principali [18-28 cm]", "Piatti da Portata [> 30 cm]", "Piatti Rettangolari")
            val Ciotole = listOf("Ciotole Kobachi e Antipasti [< 13 cm]", "Ciotole da Portata [> 13 cm]", "Ciotole da Zuppa", "Ciotole da Riso", "Ciotole Donburi", "Ciotale Ramen", "Ciotole Chawanmushi", "Set Ciotole Oryoki", "Soba-Choko")
            val Posate = listOf("Bacchette", "Poggia Bacchette", "Cucchiai")
            val Vassoi = listOf("Vassoi da Servizio", "Vassoi per Soba", "Bento Box")
            val Pentole = listOf("Donabe", "Padelle per Tamagoyaki","Padelle Tamagoyaki","Padelle Taiyaki", "Wok")
            val Te = listOf("Set da Tè", "Tazzine da Tè", "Teiere", "Set per il Tè Matcha", "Ciotola Matcha (Chawan)", "Scatole per il Tè (Natsune)")
            val Sake = listOf("Set da Sake", "Tazzine da Sake", "Caraffe da Sake")
            val Decorazioni = listOf("Bambole", "Piatti Decorativi", "Vasi da Fiori", "Campane del Vento", "Bruciatori di Incenso")
            val Utensili = listOf("Taglieri","Mortai e Pestelli", "Contenitori per Salse", "Contenitori per Spezie", "Griglie da Tavolo", "Libri")
            val Coltelli = listOf("Yanagi (Sushi)", "Santoku", "Bunka", "Deba", "Nakiri", "Usuba", "Honesuki/Garasuki", "Takohiki/Fuguhiki", "Mukimono", "Honekiri/Hamokiri", "Gyuto", "Sujihiki", "Petty", "Yo-Deba", "Pietre per Affilare")
            val Ingredienti = listOf("Riso","Noodles", "Salse", "Spezie e Condimenti","Brodi già Pronti", "Alghe", "Tofu e Miso", "Sake", "Tè", "Mochi")

            listaCategorieSecondarie["Piatti"] = Piatti
            listaCategorieSecondarie["Ciotole"] = Ciotole
            listaCategorieSecondarie["Posate"] = Posate
            listaCategorieSecondarie["Vassoi e Bento"] = Vassoi
            listaCategorieSecondarie["Tè"] = Te
            listaCategorieSecondarie["Sake"] = Sake
            listaCategorieSecondarie["Coltelli"] = Coltelli
            listaCategorieSecondarie["Pentole e Padelle"] = Pentole
            listaCategorieSecondarie["Utensili"] = Utensili
            listaCategorieSecondarie["Ingredienti"] = Ingredienti
            listaCategorieSecondarie["Decorazioni Interne"] = Decorazioni

            return listaCategorieSecondarie
        }
}