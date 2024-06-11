package com.example.myanimelist.data.repository

import com.example.myanimelist.data.model.Anime

class AnimeRepository {

    fun getAllData(): List<Anime> {
        return listOf(
            Anime(
                photoUrl = "https://www.crunchyroll.com/imgsrv/display/thumbnail/1200x675/catalog/crunchyroll/fdc0dff409f19dfd8ffff5037257ac98.jpe"
            ),
            Anime(
                photoUrl = "https://m.media-amazon.com/images/S/pv-target-images/f6229414d529451c2677a18db912a09fc8ee78a78669cf7043acbc8d952a6f8c.jpg"
            ),
            Anime(
                photoUrl = "https://puui.wetvinfo.com/vcover_hz_pic/0/dk9dwf1131racnz1615443548288/0"
            ),
            Anime(
                photoUrl = "https://www.mangamag.fr/wp-content/uploads/2022/02/Liste-episodes-demon-slayer-1.png.jpg"
            ),
            Anime(
                photoUrl = "https://meups.com.br/wp-content/uploads/2022/08/Cyberpunk.jpg"
            ),
            Anime(
                photoUrl = "https://m.media-amazon.com/images/S/pv-target-images/cc1a5f842ea4e49fadc4db30a374fd23586fdfbd8f921cfdbb7ddcc51afdc064.jpg"
            ),
            Anime(
                photoUrl = "https://imgsrv.crunchyroll.com/cdn-cgi/image/fit=contain,format=auto,quality=85,width=1200,height=675/catalog/crunchyroll/aa3f7282fe345e81a1addada43bfad92.jpe"
            ),
            Anime(
                photoUrl = "https://static0.gamerantimages.com/wordpress/wp-content/uploads/2024/03/konosuba-season-3-feature.jpg"
            ),
            Anime(
                photoUrl = "https://static.wikia.nocookie.net/liberproeliis/images/b/bd/Akame_ga_kill_by_marcus_sen-d8a239n.png/revision/latest/scale-to-width-down/680?cb=20161211100737&path-prefix=pt-br"
            ),
            Anime(
                photoUrl = "https://dimensaosete.com.br/static/97d33cbc5d14ee7035a9843d05c95412/d8eb3/baki-manga.webp"
            ),
            Anime(
                photoUrl = "https://bilder.fernsehserien.de/sendung/hr2/darling-in-the-franxx_226689.png"
            ),
            Anime(
                photoUrl = "https://m.media-amazon.com/images/S/pv-target-images/7730382bf4459ad9f445f7a7fe5b814c7ff22bbac43cf91667e6ea30c718fb12.jpg"
            )
        )
    }

}