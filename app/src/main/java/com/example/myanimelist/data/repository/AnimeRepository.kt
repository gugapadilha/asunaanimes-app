package com.example.myanimelist.data.repository

import com.example.myanimelist.domain.model.Anime

class AnimeRepository {

    fun getAllData(): List<Anime> {
        return listOf(
            Anime(
                name = "Fate Zero",
                desc = "Fate/Zero is set ten years before the events of Fate/stay night, and tells the story of the Fourth Holy Grail War, a secret magical tournament held in Fuyuki City, Japan, where seven mages known as Masters summon Servants, reincarnations of legendary souls and heroes from all across time",
                order = 1,
                photoUrl = "https://www.crunchyroll.com/imgsrv/display/thumbnail/1200x675/catalog/crunchyroll/fdc0dff409f19dfd8ffff5037257ac98.jpe"
            ),
            Anime(
                name = "Code Geass",
                desc = "Harboring a thirst for revenge against the Brittanian Empire for their actions against his family, Lelouch wages war against the Empire. Using his newfound power and tactical ability, Lelouch goes under the alias Zero in attempts to fulfill his childhood vow, to destroy Brittannia!",
                order = 2,
                photoUrl = "https://m.media-amazon.com/images/S/pv-target-images/f6229414d529451c2677a18db912a09fc8ee78a78669cf7043acbc8d952a6f8c.jpg"
            ),
            Anime(
                name = "Sword Art Online",
                desc = "SWORD ART ONLINE is the story of a multiplayer virtual-reality game that takes a deadly turn when players discover they can't escape of their own will but must play to victory or to death. It centers on Kirito",
                order = 3,
                photoUrl = "https://puui.wetvinfo.com/vcover_hz_pic/0/dk9dwf1131racnz1615443548288/0"
            ),
            Anime(
                name = "Kimetsu no Yaiba",
                desc = "A family is attacked by demons and only two members survive - Tanjiro and his sister Nezuko, who is turning into a demon slowly. Tanjiro sets out to become a demon slayer to avenge his family and cure his sister.",
                order = 4,
                photoUrl = "https://static.wikia.nocookie.net/tsrd/images/a/a3/Kimetsu-no-yaiba-temporada-3-kanroji-kokushibo_zu3m.1200.webp/revision/latest?cb=20230607183216&path-prefix=pt-br"
            ),
            Anime(
                name = "Cyberpunk Edgerunners",
                desc = "Is set in Night City, a self-reliant metropolis located in the Free State of California that suffers from extensive corruption, cybernetic addiction, and gang violence. The city is split into six districts, each of which has its own precise living requirements, and is controlled by several megacorporations, including Arasaka and its rival Militech.",
                order = 5,
                photoUrl = "https://meups.com.br/wp-content/uploads/2022/08/Cyberpunk.jpg"
            ),
            Anime(
                name = "Fate Stay Night",
                desc = "The story revolves around Shirou Emiya, a hardworking and honest teenager who unwillingly enters a to-the-death tournament called the Fifth Holy Grail War, where combatants fight with magic and Heroes from throughout history for a chance to have their wishes granted by the eponymous Holy Grail.",
                order = 6,
                photoUrl = "https://m.media-amazon.com/images/S/pv-target-images/cc1a5f842ea4e49fadc4db30a374fd23586fdfbd8f921cfdbb7ddcc51afdc064.jpg"
            ),
            Anime(
                name = "Eighty Six",
                desc = "The Republic of San Magnolia has been at war with the Giadian Empire for nine years. Though it initially suffered devastating losses to the Empire's autonomous mechanized Legions, The Republic has since developed its own autonomous units—Juggernauts—which are directed remotely by a Handler. While on the surface the public believes the war is being fought between machines.",
                order = 7,
                photoUrl = "https://imgsrv.crunchyroll.com/cdn-cgi/image/fit=contain,format=auto,quality=85,width=1200,height=675/catalog/crunchyroll/aa3f7282fe345e81a1addada43bfad92.jpe"
            ),
            Anime(
                name = "Konosuba",
                desc = "Kazuma Satou, a high school student, gamer and shut-in, died on an average day walking home from school, performing the one heroic act of his life. He reawakens in the afterlife, greeted by a beautiful but rude girl named Aqua, who claims to be a goddess that guides the youth whose lives got cut short.",
                order = 8,
                photoUrl = "https://image.api.playstation.com/vulcan/ap/rnd/202310/0513/90b39ae2768076228bd844bd7e18eb36ecaa18ecbfe17b73.jpg"
            ),
            Anime(
                name = "Akame ga Kill",
                desc = "A countryside boy named Tatsumi sets out on a journey to The Capital to make a name for himself and met a seemingly dangerous group of Assassins known as Night Raid. Their journey begins.",
                order = 9,
                photoUrl = "https://static.wikia.nocookie.net/liberproeliis/images/b/bd/Akame_ga_kill_by_marcus_sen-d8a239n.png/revision/latest/scale-to-width-down/680?cb=20161211100737&path-prefix=pt-br"
            ),
            Anime(
                name = "Baki",
                desc = "Since his birth, Baki has been training so that he may one day surpass his father Yuujirou and earn his love. Baki's mother, Emi Akezawa made sure that he had the most advanced training equipment around. She has provided him throughout his life with the best martial arts teachers, personal gym trainers, and sports equipment that money can buy",
                order = 10,
                photoUrl = "https://dimensaosete.com.br/static/97d33cbc5d14ee7035a9843d05c95412/d8eb3/baki-manga.webp"
            ),
            Anime(
                name = "Darling in the Franxx",
                desc = "Darling in the Franxx is set in a dystopian future where children are artificially created and indoctrinated solely to defend the remnants of civilization. The story follows a squad of ten pilots, particularly focusing on the partnership between Hiro, a former prodigy, and Zero Two, a hybrid human and elite pilot who aspires to become entirely human.",
                order = 11,
                photoUrl = "https://bilder.fernsehserien.de/sendung/hr2/darling-in-the-franxx_226689.png"
            ),
            Anime(
                name = "Mushoku Tensei",
                desc = "An unnamed obese 34-year-old Japanese NEET is evicted from his home by his four siblings following his parents' deaths and skipping the funeral service. Upon some self-introspection, he concluded his life was ultimately pointless but still intercepts a speeding truck heading towards a group of teenagers in an attempt to do something meaningful for once in his life and manages to pull one of them out of harm's way before dying.",
                order = 12,
                photoUrl = "https://criticalhits.com.br/wp-content/uploads/2022/03/mushoku-8.webp"
            ),
            Anime(
                name = "Solo Leveling",
                desc = "Follows the adventures Sung Jinwoo in a world that is constantly threatened by monsters and the evil forces. In his battles Sung transforms himself from weakest hunter of all mankind",
                order = 13,
                photoUrl = "https://imgsrv.crunchyroll.com/cdn-cgi/image/fit=contain,format=auto,quality=85,width=1200,height=675/catalog/crunchyroll/4305090653ee4239dd0d797f1a4a6bdf.jpe"
            ),
            Anime(
                name = "Haikyu",
                desc = "Junior high school student, Shoyo Hinata, becomes obsessed with volleyball after catching a glimpse of Karasuno High School playing in the Nationals on TV. Of short stature himself, Hinata is inspired by a player the commentators nickname 'The Little Giant', Karasuno's short but talented wing spiker.",
                order = 14,
                photoUrl = "https://ovicio.com.br/wp-content/uploads/2024/02/20240208-haikyuu-final-img1-1024x576.jpg"
            ),
        )
    }

}