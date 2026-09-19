package com.example.data.education

import com.example.core.i18n.LocalizedText
import com.example.data.models.*

object EducationalRepository {

    val worlds: List<WorldDefinition> = listOf(
        WorldDefinition(
            id = "alphabet",
            name = LocalizedText("مدينة الحروف", "La Ville des Lettres", "Alphabet City"),
            description = LocalizedText("اجمع الحروف واكتشف الكلمات الأولى الممتعة!", "Attrape les lettres et forme tes premiers mots !", "Collect letters and discover fun first words!"),
            themeEmoji = "🔤",
            primaryColorHex = 0xFFFF7043,
            secondaryColorHex = 0xFFFFB74D,
            skyColorHex = 0xFF81D4FA,
            groundColorHex = 0xFF81C784,
            requiredLevel = 1,
            categoryTag = "alphabet"
        ),
        WorldDefinition(
            id = "words",
            name = LocalizedText("وادي الكلمات", "La Vallée des Mots", "Word Valley"),
            description = LocalizedText("تعلّم الكلمات ونطقها بالعربية والفرنسية والإنجليزية!", "Apprends les mots en arabe, français et anglais !", "Learn words and vocabulary in 3 languages!"),
            themeEmoji = "📖",
            primaryColorHex = 0xFF42A5F5,
            secondaryColorHex = 0xFF90CAF9,
            skyColorHex = 0xFFB3E5FC,
            groundColorHex = 0xFFAED581,
            requiredLevel = 2,
            categoryTag = "words"
        ),
        WorldDefinition(
            id = "animals",
            name = LocalizedText("مملكة الحيوانات", "Le Royaume des Animaux", "Animal Kingdom"),
            description = LocalizedText("استكشف الأسود والزرافات والفيلة في البرية الجميلة!", "Explore les lions, girafes et éléphants dans la savane !", "Explore lions, giraffes and elephants in the wild!"),
            themeEmoji = "🦁",
            primaryColorHex = 0xFF66BB6A,
            secondaryColorHex = 0xFFA5D6A7,
            skyColorHex = 0xFFB2EBF2,
            groundColorHex = 0xFF8D6E63,
            requiredLevel = 3,
            categoryTag = "animals"
        ),
        WorldDefinition(
            id = "insects",
            name = LocalizedText("حديقة الحشرات", "Le Jardin des Insectes", "Insect Garden"),
            description = LocalizedText("عالم النحل المجد والدعسوقة الملونة والفراشات الرائعة!", "Le monde des abeilles, coccinelles et papillons !", "The buzzing world of bees, ladybugs, and butterflies!"),
            themeEmoji = "🐞",
            primaryColorHex = 0xFF26A69A,
            secondaryColorHex = 0xFF80CBC4,
            skyColorHex = 0xFFC8E6C9,
            groundColorHex = 0xFF689F38,
            requiredLevel = 4,
            categoryTag = "insects"
        ),
        WorldDefinition(
            id = "vehicles",
            name = LocalizedText("مدينة المركبات", "La Cité des Véhicules", "Vehicle City"),
            description = LocalizedText("سيارات الإسعاف، الطائرات، القطارات، والسفن السريعة!", "Voitures, avions, trains et navires rapides !", "Ambulances, planes, trains, and speedy ships!"),
            themeEmoji = "🚗",
            primaryColorHex = 0xFFEF5350,
            secondaryColorHex = 0xFFFF8A80,
            skyColorHex = 0xFF90CAF9,
            groundColorHex = 0xFF78909C,
            requiredLevel = 5,
            categoryTag = "vehicles"
        ),
        WorldDefinition(
            id = "algeria",
            name = LocalizedText("اكتشف الجزائر", "Découvre l'Algérie", "Discover Algeria"),
            description = LocalizedText("الجزائر العاصمة، جسور قسنطينة، وهران، والرمال الذهبية!", "Alger, Constantine, Oran, et les dunes dorées du Sahara !", "Algiers, Constantine bridges, Oran, and the Sahara!"),
            themeEmoji = "🇩🇿",
            primaryColorHex = 0xFF2E7D32,
            secondaryColorHex = 0xFFD32F2F,
            skyColorHex = 0xFF81D4FA,
            groundColorHex = 0xFFFFD54F,
            requiredLevel = 6,
            categoryTag = "algeria"
        ),
        WorldDefinition(
            id = "world_geography",
            name = LocalizedText("اكتشف العالم", "Le Tour du Monde", "Discover the World"),
            description = LocalizedText("خريطة تفاعلية للقارات، المحيطات، والمعالم العالمية المشهورة!", "Carte interactive des continents, océans et merveilles !", "Interactive map of continents, oceans, and world wonders!"),
            themeEmoji = "🌍",
            primaryColorHex = 0xFF0288D1,
            secondaryColorHex = 0xFF4FC3F7,
            skyColorHex = 0xFF80DEEA,
            groundColorHex = 0xFF81C784,
            requiredLevel = 7,
            categoryTag = "world"
        ),
        WorldDefinition(
            id = "science",
            name = LocalizedText("عالم العلوم", "L'Univers des Sciences", "World of Science"),
            description = LocalizedText("جسم الإنسان، النباتات، الماء، الكهرباء والطاقة النظيفة!", "Le corps humain, les plantes, l'eau et l'énergie !", "Human body, green plants, water cycle, and clean energy!"),
            themeEmoji = "🔬",
            primaryColorHex = 0xFF7E57C2,
            secondaryColorHex = 0xFFB39DDB,
            skyColorHex = 0xFFD1C4E9,
            groundColorHex = 0xFF5C6BC0,
            requiredLevel = 8,
            categoryTag = "science"
        ),
        WorldDefinition(
            id = "space",
            name = LocalizedText("عالم الفضاء", "L'Odyssée Spatiale", "Space Odyssey"),
            description = LocalizedText("انطلق بين الكواكب والنجوم والمجرات البعيدة على متن المركبة!", "Voyage entre les planètes, étoiles et galaxies lointaines !", "Blast off among planets, stars, and cosmic galaxies!"),
            themeEmoji = "🚀",
            primaryColorHex = 0xFF3F51B5,
            secondaryColorHex = 0xFF7986CB,
            skyColorHex = 0xFF1A237E,
            groundColorHex = 0xFF311B92,
            requiredLevel = 9,
            categoryTag = "space"
        ),
        WorldDefinition(
            id = "prayer",
            name = LocalizedText("تعلّم الصلاة والوضوء", "Apprendre la Prière", "Learn Prayer & Wudhu"),
            description = LocalizedText("خطوات الوضوء الطاهر وحركات الصلاة وآداب المسجد بهدوء ووقار.", "Les étapes des ablutions et les gestes de la prière avec paix.", "Steps of purification (Wudhu) and prayer in a peaceful atmosphere."),
            themeEmoji = "🕌",
            primaryColorHex = 0xFF00897B,
            secondaryColorHex = 0xFF4DB6AC,
            skyColorHex = 0xFFB2DFDB,
            groundColorHex = 0xFF00695C,
            requiredLevel = 10,
            categoryTag = "prayer"
        ),
        WorldDefinition(
            id = "trivia",
            name = LocalizedText("المعلومات العامة", "Culture Générale", "General Knowledge Trivia"),
            description = LocalizedText("تحدي الأبطال في الثقافة والابتكارات والذكاء وحل الألغاز!", "Défie ton esprit avec des quiz amusants et surprenants !", "Challenge your mind with fun trivia and discoveries!"),
            themeEmoji = "🧠",
            primaryColorHex = 0xFFF57C00,
            secondaryColorHex = 0xFFFFB74D,
            skyColorHex = 0xFFFFE082,
            groundColorHex = 0xFFFF8F00,
            requiredLevel = 11,
            categoryTag = "trivia"
        )
    )

    val heroes: List<HeroDefinition> = listOf(
        HeroDefinition(
            id = "zaki",
            name = LocalizedText("زاكي المستكشف", "Zaki l'Explorateur", "Zaki the Explorer"),
            avatarEmoji = "👦",
            description = LocalizedText("ذكي وسريع، يعشق استكشاف الآثار والخرائط!", "Vif et curieux, passionné de cartes et d'aventures !", "Quick and curious, loves maps and adventurous tracks!"),
            requiredLevel = 1,
            requiredCoins = 0,
            bodyColorHex = 0xFFF97316,
            shirtColorHex = 0xFF1E3A8A
        ),
        HeroDefinition(
            id = "sarah",
            name = LocalizedText("سارة العالمة", "Sarah la Scientifique", "Sarah the Scholar"),
            avatarEmoji = "👧",
            description = LocalizedText("شغوفة بالعلوم والفضاء والحيوانات وتحب التحدي!", "Passionnée de sciences, de nature et d'espace !", "Loves nature, animals, stars, and bright experiments!"),
            requiredLevel = 1,
            requiredCoins = 0,
            bodyColorHex = 0xFFEC4899,
            shirtColorHex = 0xFF0D9488
        ),
        HeroDefinition(
            id = "adam",
            name = LocalizedText("آدم البطل", "Adam le Champion", "Adam the Champion"),
            avatarEmoji = "🏃",
            description = LocalizedText("عداء قوي يتميز بالسرعة وتجاوز أصعب العقبات!", "Un grand coureur rapide face à tous les obstacles !", "Powerful runner who glides past the toughest barriers!"),
            requiredLevel = 3,
            requiredCoins = 250,
            bodyColorHex = 0xFF3B82F6,
            shirtColorHex = 0xFFEAB308
        ),
        HeroDefinition(
            id = "maya",
            name = LocalizedText("مايا الرائدة", "Maya la Pionnière", "Maya the Pioneer"),
            avatarEmoji = "👩‍🚀",
            description = LocalizedText("مستكشفة الفضاء الطموحة التي تسعى للوصول لأعلى النجوم!", "Exploratrice de l'espace visant les étoiles !", "Ambitious cosmic voyager aiming for the brightest stars!"),
            requiredLevel = 5,
            requiredCoins = 500,
            bodyColorHex = 0xFF8B5CF6,
            shirtColorHex = 0xFF10B981
        )
    )

    val vehicles: List<VehicleDefinition> = listOf(
        VehicleDefinition(
            id = "scooter",
            name = LocalizedText("سكوتر الانطلاق", "Trottinette Rapide", "Swift Scooter"),
            vehicleEmoji = "🛴",
            speedMultiplier = 1.05f,
            requiredLevel = 1,
            requiredCoins = 0
        ),
        VehicleDefinition(
            id = "rover",
            name = LocalizedText("عربة الصحراء", "Rover Saharien", "Desert Rover"),
            vehicleEmoji = "🚙",
            speedMultiplier = 1.15f,
            requiredLevel = 4,
            requiredCoins = 300
        ),
        VehicleDefinition(
            id = "speeder",
            name = LocalizedText("مركبة الفضاء الطائرة", "Planeur Cosmique", "Cosmic Speeder"),
            vehicleEmoji = "🛸",
            speedMultiplier = 1.25f,
            requiredLevel = 7,
            requiredCoins = 600
        )
    )

    val questions: List<EducationalQuestion> = listOf(
        // Level 1: Alphabet & Colors & Animals (5-6 yrs)
        EducationalQuestion(
            id = "q_alpha_1",
            worldId = "alphabet",
            ageGroup = AgeGroup.LEVEL_1,
            question = LocalizedText("ما الكلمة التي تبدأ بحرف (أ)؟", "Quel mot commence par la lettre A ?", "Which word starts with the letter A?"),
            choices = listOf(
                LocalizedText("أسد", "Lion", "Lion"),
                LocalizedText("بطة", "Canard", "Duck"),
                LocalizedText("فيل", "Éléphant", "Elephant")
            ),
            choiceIcons = listOf("🦁", "🦆", "🐘"),
            correctIndex = 0,
            hint = LocalizedText("ملك الغابة القوي! 🦁", "Le roi de la savane !", "The roaring king of the jungle!"),
            explanation = LocalizedText("حرف الألف (أ) يبدأ به: أسد، أرنب، أم.", "La lettre A commence avec : Lion (Asad), Agneau.", "The letter A begins words like Apple, Antelope."),
            category = "alphabet"
        ),
        EducationalQuestion(
            id = "q_alpha_2",
            worldId = "alphabet",
            ageGroup = AgeGroup.LEVEL_1,
            question = LocalizedText("ما هو لون الموز الناضج؟ 🍌", "De quelle couleur est une banane mûre ? 🍌", "What color is a ripe banana? 🍌"),
            choices = listOf(
                LocalizedText("أصفر", "Jaune", "Yellow"),
                LocalizedText("أزرق", "Bleu", "Blue"),
                LocalizedText("أخضر", "Vert", "Green")
            ),
            choiceIcons = listOf("🟡", "🔵", "🟢"),
            correctIndex = 0,
            hint = LocalizedText("لون الشمس الساطعة في السماء! ☀️", "La couleur du soleil !", "The color of the bright sun!"),
            explanation = LocalizedText("الموز الناضج لونه أصفر جميل ولذيذ!", "Une bonne banane mûre est jaune !", "Ripe bananas have a bright yellow color!"),
            category = "colors"
        ),
        EducationalQuestion(
            id = "q_anim_1",
            worldId = "animals",
            ageGroup = AgeGroup.LEVEL_1,
            question = LocalizedText("من هو أطول حيوان في العالم؟", "Quel est l'animal le plus grand en taille ?", "Which is the tallest animal in the world?"),
            choices = listOf(
                LocalizedText("الزرافة", "La girafe", "Giraffe"),
                LocalizedText("السلحفاة", "La tortue", "Turtle"),
                LocalizedText("الأرنب", "Le lapin", "Rabbit")
            ),
            choiceIcons = listOf("🦒", "🐢", "🐇"),
            correctIndex = 0,
            hint = LocalizedText("تصل رقبتها الطويلة لأعالي الأشجار! 🦒", "Son très long cou atteint les branches !", "Its long neck reaches high into acacia trees!"),
            explanation = LocalizedText("الزرافة هي أطول حيوان بري على كوكب الأرض بفضل رقبتها الرائعة!", "La girafe est le plus grand mammifère terrestre !", "Giraffes are the tallest mammals on Earth!"),
            category = "animals"
        ),
        EducationalQuestion(
            id = "q_veh_1",
            worldId = "vehicles",
            ageGroup = AgeGroup.LEVEL_1,
            question = LocalizedText("أي مركبة تطير في السماء بين السحب؟", "Quel véhicule vole dans le ciel ?", "Which vehicle flies up in the sky?"),
            choices = listOf(
                LocalizedText("طائرة", "Avion", "Airplane"),
                LocalizedText("قطار", "Train", "Train"),
                LocalizedText("دراجة", "Vélo", "Bicycle")
            ),
            choiceIcons = listOf("✈️", "🚆", "🚲"),
            correctIndex = 0,
            hint = LocalizedText("لديها أجنحة ومحركات قوية! ✈️", "Elle possède deux grandes ailes !", "It has large wings and jet engines!"),
            explanation = LocalizedText("الطائرة تحلق عالياً في الجو لتنقل المسافرين عبر العالم بسرعة!", "L'avion transporte les voyageurs à travers les cieux !", "Airplanes fly passengers across continents!"),
            category = "vehicles"
        ),

        // Level 2: Word building, Insects, Algeria, Science (7-8 yrs)
        EducationalQuestion(
            id = "q_alg_1",
            worldId = "algeria",
            ageGroup = AgeGroup.LEVEL_2,
            question = LocalizedText("ما هي عاصمة الجزائر الحبيبة؟", "Quelle est la capitale de l'Algérie ?", "What is the capital city of Algeria?"),
            choices = listOf(
                LocalizedText("الجزائر العاصمة", "Alger", "Algiers"),
                LocalizedText("وهران", "Oran", "Oran"),
                LocalizedText("قسنطينة", "Constantine", "Constantine")
            ),
            choiceIcons = listOf("🏛️", "🌊", "🌉"),
            correctIndex = 0,
            hint = LocalizedText("تُلقب بالبهجة والبيضاء وتطل على البحر الأبيض المتوسط!", "La ville blanche surnommée El Bahdja !", "Known as Algiers the White (El Bahdja)!"),
            explanation = LocalizedText("الجزائر العاصمة هي العاصمة التاريخية وتشتهر بمقام الشهيد وقصبة الجزائر العريقة.", "Alger est la capitale et abrite la Casbah historique et Maqam Echahid.", "Algiers is the historic capital featuring the Martyrs' Memorial."),
            category = "algeria"
        ),
        EducationalQuestion(
            id = "q_alg_2",
            worldId = "algeria",
            ageGroup = AgeGroup.LEVEL_2,
            question = LocalizedText("أي مدينة جزائرية تشتهر بلقب (مدينة الجسور المعلقة)؟", "Quelle ville algérienne est la cité des ponts suspendus ?", "Which Algerian city is famous as the City of Bridges?"),
            choices = listOf(
                LocalizedText("قسنطينة", "Constantine", "Constantine"),
                LocalizedText("عنابة", "Annaba", "Annaba"),
                LocalizedText("تلمسان", "Tlemcen", "Tlemcen")
            ),
            choiceIcons = listOf("🌉", "🌴", "🏰"),
            correctIndex = 0,
            hint = LocalizedText("مدينة عريقة مبنية فوق صخرة صلدة يربط بينها جسور مذهلة!", "Une cité millénaire au-dessus des gorges du Rhumel !", "Built over dramatic canyon gorges linked by stunning bridges!"),
            explanation = LocalizedText("قسنطينة مدينة الصخر العتيق تشتهر بجسورها المعلقة كجسور سيدي راشد وسيدي مسيد!", "Constantine est mondialement connue pour ses ponts majestueux.", "Constantine is famed worldwide for its awe-inspiring suspension bridges."),
            category = "algeria"
        ),
        EducationalQuestion(
            id = "q_ins_1",
            worldId = "insects",
            ageGroup = AgeGroup.LEVEL_2,
            question = LocalizedText("ما هي الحشرة النافعة التي تصنع العسل اللذيذ والشفائي؟", "Quel insecte travailleur produit du bon miel ?", "Which industrious insect makes delicious sweet honey?"),
            choices = listOf(
                LocalizedText("النحلة", "L'abeille", "Honeybee"),
                LocalizedText("الجراد", "Le criquet", "Grasshopper"),
                LocalizedText("الذباب", "La mouche", "Housefly")
            ),
            choiceIcons = listOf("🐝", "🦗", "🪰"),
            correctIndex = 0,
            hint = LocalizedText("تزور الأزهار وتعيش في خلية منظمة ولها صوت طنين! 🐝", "Elle butine les fleurs et vit dans une ruche !", "Visits colorful flowers and lives in a buzzing hive!"),
            explanation = LocalizedText("النحلة تصنع العسل الطبيعي وتساعد في تلقيح النباتات والأزهار!", "Les abeilles jouent un rôle vital pour la planète en pollinisant les plantes !", "Honeybees pollinate plants and craft nutritious natural honey!"),
            category = "insects"
        ),
        EducationalQuestion(
            id = "q_sci_1",
            worldId = "science",
            ageGroup = AgeGroup.LEVEL_2,
            question = LocalizedText("ماذا تحتاج النباتات الخضراء لتصنع غذاءها مع ضوء الشمس؟", "De quoi les plantes ont-elles besoin pour grandir avec le soleil ?", "What do green plants need to make food alongside sunlight?"),
            choices = listOf(
                LocalizedText("الماء والهواء", "De l'eau et de l'air", "Water and Air"),
                LocalizedText("الحليب والعصير", "Du lait et du jus", "Milk and Juice"),
                LocalizedText("الرمل فقط", "Juste du sable", "Sand only")
            ),
            choiceIcons = listOf("💧", "🥛", "🏜️"),
            correctIndex = 0,
            hint = LocalizedText("نسقيها به كل يوم لترتوي جذورها! 💧", "On l'arrose régulièrement pour ses racines !", "We water it regularly to nourish its roots!"),
            explanation = LocalizedText("تقوم النباتات بعملية التركيب الضوئي باستخدام ضوء الشمس والماء وثاني أكسيد الكربون.", "La photosynthèse utilise l'eau et la lumière du soleil.", "Photosynthesis uses sunlight, water, and air to produce food and oxygen!"),
            category = "science"
        ),

        // Level 3: Space, World Geography, History, Prayer (9-10 yrs)
        EducationalQuestion(
            id = "q_spc_1",
            worldId = "space",
            ageGroup = AgeGroup.LEVEL_3,
            question = LocalizedText("ما هو الكوكب الأقرب إلى الشمس في نظامنا الشمسي؟", "Quelle est la planète la plus proche du Soleil ?", "Which planet is closest to the Sun in our solar system?"),
            choices = listOf(
                LocalizedText("عطارد", "Mercure", "Mercury"),
                LocalizedText("المريخ", "Mars", "Mars"),
                LocalizedText("المشتري", "Jupiter", "Jupiter")
            ),
            choiceIcons = listOf("🪐", "🔴", "🟠"),
            correctIndex = 0,
            hint = LocalizedText("كوكب صخري صغير يدور بسرعة فائقة حول الشمس!", "Une petite planète rocheuse très rapide !", "Small rocky planet orbiting the Sun in just 88 days!"),
            explanation = LocalizedText("عطارد هو أقرب الكواكب للشمس، ويليه الزهرة ثم كوكبنا الأرض.", "Mercure est la première planète la plus proche du Soleil.", "Mercury is the innermost and smallest planet in the Solar System."),
            category = "space"
        ),
        EducationalQuestion(
            id = "q_wld_1",
            worldId = "world_geography",
            ageGroup = AgeGroup.LEVEL_3,
            question = LocalizedText("ما هو أكبر محيط مائي على سطح كوكب الأرض؟", "Quel est le plus grand océan de notre Terre ?", "What is the largest ocean on Earth?"),
            choices = listOf(
                LocalizedText("المحيط الهادئ", "L'océan Pacifique", "Pacific Ocean"),
                LocalizedText("المحيط الأطلسي", "L'océan Atlantique", "Atlantic Ocean"),
                LocalizedText("المحيط الهندي", "L'océan Indien", "Indian Ocean")
            ),
            choiceIcons = listOf("🌊", "🚢", "🏝️"),
            correctIndex = 0,
            hint = LocalizedText("اسمه يدل على الهدوء وهو أوسع مساحة مائية زرقاء!", "Son nom évoque la paix et il couvre un tiers du globe !", "Its name means peaceful and it covers over 30% of the Earth's surface!"),
            explanation = LocalizedText("المحيط الهادئ هو الأكبر مساحة ويغطي أكثر من ثلث مساحة الكرة الأرضية.", "L'océan Pacifique est le plus vaste océan de notre planète.", "The Pacific Ocean is larger than all Earth's landmasses combined."),
            category = "world"
        ),
        EducationalQuestion(
            id = "q_pray_1",
            worldId = "prayer",
            ageGroup = AgeGroup.LEVEL_3,
            question = LocalizedText("ما هي أول خطوة يبدأ بها المسلم وضوءه المبارك؟", "Quelle est la première étape pour commencer les ablutions ?", "What is the very first step when starting Wudhu (purification)?"),
            choices = listOf(
                LocalizedText("النية وغسل الكفين ثلاثاً", "L'intention et laver les mains", "Intention and washing hands"),
                LocalizedText("مسح الرأس مباشرة", "Essuyer la tête", "Wiping head"),
                LocalizedText("غسل القدمين", "Laver les pieds", "Washing feet")
            ),
            choiceIcons = listOf("🤲", "💆", "🦶"),
            correctIndex = 0,
            hint = LocalizedText("ننوي بقلبنا ونغسل يدينا النظيفتين قبل كل شيء!", "On purifie ses mains et formule l'intention avec son cœur.", "Cleanse the hands and make sincere intention!"),
            explanation = LocalizedText("نبدأ الوضوء بالنية والتسمية ثم غسل الكفين ثلاث مرات حرصاً على النظافة والسكينة.", "On commence par l'intention et le lavage méticuleux des mains.", "We start with sincere intention and wash both hands three times."),
            category = "prayer"
        )
    )

    val prayerSteps: List<PrayerStep> = listOf(
        PrayerStep(1, LocalizedText("النية والتسمية", "Intention & Bismillah", "Intention & Bismillah"), LocalizedText("أنوي الوضوء بقلبي وأقول بسم الله الرحمن الرحيم", "Avoir l'intention dans le cœur et dire Bismillah", "Make intention in heart and say Bismillah"), "🤲", true),
        PrayerStep(2, LocalizedText("غسل الكفين ثلاثاً", "Laver les mains (3x)", "Wash hands (3x)"), LocalizedText("غسل الكفين جيداً بين الأصابع ثلاث مرات", "Bien laver les mains et entre les doigts trois fois", "Wash hands up to wrists thoroughly three times"), "🧼", true),
        PrayerStep(3, LocalizedText("المضمضة والاستنشاق", "Rincer bouche et nez (3x)", "Rinse mouth & nose (3x)"), LocalizedText("مضمضة الفم بالماء النظيف واستنشاق الأنف بلطف", "Rincer la bouche et le nez avec de l'eau claire trois fois", "Gently rinse mouth and nose with clean water"), "💧", true),
        PrayerStep(4, LocalizedText("غسل الوجه كاملاً", "Laver le visage (3x)", "Wash full face (3x)"), LocalizedText("غسل الوجه من منبت الشعر إلى الذقن ثلاثاً", "Laver le visage des cheveux jusqu'au menton", "Wash face from hairline down to chin three times"), "😊", true),
        PrayerStep(5, LocalizedText("غسل اليدين إلى المرفقين", "Laver les bras aux coudes", "Wash arms to elbows"), LocalizedText("البدء باليد اليمنى ثم اليسرى إلى المرفق", "Commencer par le bras droit puis le gauche jusqu'au coude", "Wash right arm then left arm up to the elbows"), "💪", true),
        PrayerStep(6, LocalizedText("مسح الرأس والأذنين", "Essuyer tête et oreilles", "Wipe head & ears"), LocalizedText("مسح شعر الرأس بيدين مبللتين ثم مسح الأذنين", "Passer les mains humides sur la tête et les oreilles", "Gently wipe wet hands over head and clean ears"), "💆", true),
        PrayerStep(7, LocalizedText("غسل القدمين إلى الكعبين", "Laver les pieds aux chevilles", "Wash feet to ankles"), LocalizedText("غسل القدم اليمنى ثم اليسرى مع الكعبين ثلاثاً", "Laver soigneusement le pied droit puis le pied gauche", "Thoroughly wash right foot then left foot up to ankles"), "🦶", true)
    )

    val wordItems: List<WordMatchingItem> = listOf(
        WordMatchingItem("w1", "أسد", "Lion", "Lion", "🦁", "animals"),
        WordMatchingItem("w2", "قطة", "Cat", "Chat", "🐱", "animals"),
        WordMatchingItem("w3", "فيل", "Elephant", "Éléphant", "🐘", "animals"),
        WordMatchingItem("w4", "سيارة", "Car", "Voiture", "🚗", "vehicles"),
        WordMatchingItem("w5", "طائرة", "Airplane", "Avion", "✈️", "vehicles"),
        WordMatchingItem("w6", "صاروخ", "Rocket", "Fusée", "🚀", "space"),
        WordMatchingItem("w7", "شجرة", "Tree", "Arbre", "🌳", "science"),
        WordMatchingItem("w8", "شمس", "Sun", "Soleil", "☀️", "space")
    )
}
