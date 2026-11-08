package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class NahuatlVariantInfo(
    val id: String,
    val name: String,
    val inliCode: String,
    val regionGroup: String, // "Huasteca", "Puebla & Sierra", "Veracruz & Istmo", "Centro & Morelos", "Guerrero & Occidente", "Oaxaca & Sur"
    val states: String,
    val estimatedSpeakers: String,
    val status: String,
    val greetingPhrase: String,
    val greetingSpanish: String,
    val linguisticTrait: String,
    val description: String
)

val NAHUATL_VARIANTS_CATALOG = listOf(
    NahuatlVariantInfo(
        id = "na_huasteca_ver",
        name = "Náhuatl de la Huasteca veracruzana",
        inliCode = "Na-27",
        regionGroup = "Huasteca",
        states = "Veracruz (Chicontepec, Tantoyuca, Ixhuatlán, Benito Juárez)",
        estimatedSpeakers = "130,000+ hablantes",
        status = "Muy vigorosa / En uso cotidiano",
        greetingPhrase = "Piltzin, ¿kenke ticatej?",
        greetingSpanish = "Hola joven, ¿cómo estás?",
        linguisticTrait = "Conserva la 'TL' final, sufijo plural -mej y abundante léxico agrícola huasteco.",
        description = "Tierra sagrada del zacahuil, sones huastecos y la flor de cempasúchil."
    ),
    NahuatlVariantInfo(
        id = "na_huasteca_pot",
        name = "Náhuatl de la Huasteca potosina",
        inliCode = "Na-26",
        regionGroup = "Huasteca",
        states = "San Luis Potosí (Tamazunchale, Xilitla, Coxcatlán, Axtla)",
        estimatedSpeakers = "140,000+ hablantes",
        status = "Muy vigorosa / Transmisión generacional",
        greetingPhrase = "Piltzin, ¿quenque ticaaj?",
        greetingSpanish = "Hola, ¿cómo estás?",
        linguisticTrait = "Uso de vocales alargadas y rico vocabulario para el ritual del Xantolo.",
        description = "Celebración mística de los difuntos y profunda devoción a la Madre Tierra (Tonantzin)."
    ),
    NahuatlVariantInfo(
        id = "mex_huasteca_hid",
        name = "Mexicano de la Huasteca Hidalguense",
        inliCode = "Na-28",
        regionGroup = "Huasteca",
        states = "Hidalgo (Huejutla, Atlapexco, San Felipe Orizatlán, Jaltocán)",
        estimatedSpeakers = "175,000+ hablantes",
        status = "Muy vigorosa / Mayoritaria",
        greetingPhrase = "Nochi cualli, ¿kenke ticate?",
        greetingSpanish = "Hola, ¿cómo estás?",
        linguisticTrait = "Una de las variantes con mayor número absoluto de hablantes jóvenes.",
        description = "Corazón cultural de la Huasteca con bordados tradicionales e intercambio comercial activo."
    ),
    NahuatlVariantInfo(
        id = "na_puebla_cuetzalan",
        name = "Náhuatl de la Sierra, noreste de Puebla",
        inliCode = "Na-25",
        regionGroup = "Puebla & Sierra",
        states = "Puebla (Cuetzalan del Progreso, Zacapoaxtla, Tlatlauquitepec)",
        estimatedSpeakers = "135,000+ hablantes",
        status = "Muy vigorosa / Identidad viva",
        greetingPhrase = "Piyali, ¿ken tinemi talticpac?",
        greetingSpanish = "Hola, ¿cómo caminas sobre la tierra?",
        linguisticTrait = "Uso del saludo característico 'Piyali' y preservación de métricas poéticas.",
        description = "Famosa por los voladores de Cuetzalan, huipiles de gasa y la danza de los Quetzales."
    ),
    NahuatlVariantInfo(
        id = "na_puebla_huauchinango",
        name = "Náhuatl de la Sierra oeste de Puebla",
        inliCode = "Na-18",
        regionGroup = "Puebla & Sierra",
        states = "Puebla (Huauchinango, Zacatlán, Chiconcuautla)",
        estimatedSpeakers = "55,000+ hablantes",
        status = "Vigorosa / Comunitaria",
        greetingPhrase = "Piyali, ¿queniuhqui tinemi?",
        greetingSpanish = "Hola, ¿cómo estás?",
        linguisticTrait = "Pronunciación clara y suave de fonemas alveolares.",
        description = "Región cafetalera y florícola de la Sierra Norte de Puebla."
    ),
    NahuatlVariantInfo(
        id = "na_puebla_sierra_sur",
        name = "Náhuatl de la Sierra Negra, sur",
        inliCode = "Na-13",
        regionGroup = "Puebla & Sierra",
        states = "Puebla (Zoquitlán, Coyomeapan, Eloxochitlán)",
        estimatedSpeakers = "65,000+ hablantes",
        status = "Muy vigorosa",
        greetingPhrase = "Piyali, ¿ken ticatqui?",
        greetingSpanish = "Hola, ¿cómo estás?",
        linguisticTrait = "Acentuación tonal limpia y vocabulario de la alta montaña.",
        description = "Altas cumbres montañosas de la Sierra Negra de Tehuacán."
    ),
    NahuatlVariantInfo(
        id = "na_puebla_sierra_norte",
        name = "Náhuatl de la Sierra Negra, norte",
        inliCode = "Na-22",
        regionGroup = "Puebla & Sierra",
        states = "Puebla (Tehuacán, Tlacotepec, Vicente Guerrero)",
        estimatedSpeakers = "40,000+ hablantes",
        status = "Vigorosa",
        greetingPhrase = "Piyali, ¿ken tica tehhuatl?",
        greetingSpanish = "Hola, ¿cómo estás?",
        linguisticTrait = "Léxico compartido con la zona de Zongolica.",
        description = "Región de convergencia entre valles secos y la serranía verde."
    ),
    NahuatlVariantInfo(
        id = "na_puebla_centro",
        name = "Náhuatl del centro de Puebla",
        inliCode = "Na-20",
        regionGroup = "Puebla & Sierra",
        states = "Puebla (Cholula, San Martín Texmelucan, Huejotzingo)",
        estimatedSpeakers = "20,000+ hablantes",
        status = "En uso regional",
        greetingPhrase = "Niltze, cualli yectli tonalli",
        greetingSpanish = "Hola, hermosos y buenos días",
        linguisticTrait = "Formas reverenciales tradicionales vinculadas al Gran Popocatépetl.",
        description = "Tierras fértiles del volcán Don Goyo y las pirámides sagradas de Cholula."
    ),
    NahuatlVariantInfo(
        id = "mex_guerrero",
        name = "Mexicano de Guerrero",
        inliCode = "Na-24",
        regionGroup = "Guerrero & Occidente",
        states = "Guerrero (Chilapa, Olinalá, Zitlala, Acatlán, Tixtla)",
        estimatedSpeakers = "160,000+ hablantes",
        status = "Muy vigorosa",
        greetingPhrase = "Piyali, ¿ken nican tinemi?",
        greetingSpanish = "Hola, ¿cómo estás?",
        linguisticTrait = "Utiliza el saludo 'Piyali' y formas verbales directas y expresivas.",
        description = "Hogar del arte en laqueado de Olinalá, la pelea ritual de jaguares y la Tigrada."
    ),
    NahuatlVariantInfo(
        id = "na_veracruz_zongolica",
        name = "Náhuatl central de Veracruz",
        inliCode = "Na-23",
        regionGroup = "Veracruz & Istmo",
        states = "Veracruz (Zongolica, Tequila, Orizaba, Córdoba)",
        estimatedSpeakers = "125,000+ hablantes",
        status = "Muy vigorosa",
        greetingPhrase = "Piyali, ¿queniuhqui tica tzin?",
        greetingSpanish = "Hola, ¿cómo está usted?",
        linguisticTrait = "Sufijo honorífico -tzin muy extendido y acento poético.",
        description = "Serranía del café, neblina constante y medicina herbal ancestral."
    ),
    NahuatlVariantInfo(
        id = "na_veracruz_istmo",
        name = "Náhuatl del Istmo",
        inliCode = "Na-19",
        regionGroup = "Veracruz & Istmo",
        states = "Veracruz (Pajapan, Mecayapan, Tatahuicapan)",
        estimatedSpeakers = "45,000+ hablantes",
        status = "Muy vigorosa",
        greetingPhrase = "Piyali, ¿ken ticatej amo?",
        greetingSpanish = "Hola, ¿cómo están?",
        linguisticTrait = "Variante 'Nawat' donde el grupo consonántico 'TL' pasa a 'T'.",
        description = "Llanura del Istmo veracruzano cerca del Golfo de México."
    ),
    NahuatlVariantInfo(
        id = "na_veracruz_istmo_bajo",
        name = "Náhuatl del Istmo bajo",
        inliCode = "Na-15",
        regionGroup = "Veracruz & Istmo",
        states = "Veracruz (Cosoleacaque, Jáltipan, Zaragoza)",
        estimatedSpeakers = "14,000+ hablantes",
        status = "En uso",
        greetingPhrase = "Piyali, ¿ken tinemij amo?",
        greetingSpanish = "Hola, ¿cómo están?",
        linguisticTrait = "Simplificación de grupos vocálicos y rica tradición oral.",
        description = "Riberas de los ríos sureños de Veracruz."
    ),
    NahuatlVariantInfo(
        id = "mex_centro_cdmx",
        name = "Mexicano del centro",
        inliCode = "Na-02",
        regionGroup = "Centro & Morelos",
        states = "CDMX (Milpa Alta, Xochimilco), Estado de México",
        estimatedSpeakers = "40,000+ hablantes",
        status = "En revitalización activa",
        greetingPhrase = "Niltze, yec tlaneci",
        greetingSpanish = "Hola, bello amanecer",
        linguisticTrait = "Náhuatl clásico urbano del Anáhuac con gran corpus literario.",
        description = "Chinampas de Xochimilco y bosques milenarios de Milpa Alta."
    ),
    NahuatlVariantInfo(
        id = "mex_oriente_tlaxcala",
        name = "Mexicano del oriente",
        inliCode = "Na-01",
        regionGroup = "Centro & Morelos",
        states = "Tlaxcala (San Pablo del Monte, Contla de Juan Cuamatzi)",
        estimatedSpeakers = "25,000+ hablantes",
        status = "Vigorosa",
        greetingPhrase = "Niltze, ¿queniuhqui tinemi tehhuatl?",
        greetingSpanish = "Hola, ¿cómo caminas?",
        linguisticTrait = "Conserva pronunciación límpida de consonantes dobles.",
        description = "Faldas de la Malinche (Matlalcueyetl) y arte textil en lana."
    ),
    NahuatlVariantInfo(
        id = "mex_morelos_centro_bajo",
        name = "Mexicano del centro bajo",
        inliCode = "Na-06",
        regionGroup = "Centro & Morelos",
        states = "Morelos (Cuernavaca, Tepoztlán, Tlayacapan)",
        estimatedSpeakers = "8,000+ hablantes",
        status = "Vigorosa local",
        greetingPhrase = "Niltze, piltzin cualli tonalli",
        greetingSpanish = "Hola estimado, buen día",
        linguisticTrait = "Formas reverenciales e historiografía viva de los Tepoztecos.",
        description = "Pueblos mágicos de Morelos y cerros místicos de Tepoztlán."
    ),
    NahuatlVariantInfo(
        id = "mex_morelos_temixco",
        name = "Mexicano de Temixco",
        inliCode = "Na-12",
        regionGroup = "Centro & Morelos",
        states = "Morelos (Temixco, Cuentepec)",
        estimatedSpeakers = "4,500+ hablantes",
        status = "Muy vigorosa local",
        greetingPhrase = "Niltze, ¿ken tinemi nican?",
        greetingSpanish = "Hola, ¿cómo estás?",
        linguisticTrait = "Comunidad monolingüe y bilingüe con fuerte identidad comunitaria en Cuentepec.",
        description = "Comunidad alfarera de la zona poniente de Morelos."
    ),
    NahuatlVariantInfo(
        id = "mex_morelos_tetela",
        name = "Mexicano de Tetela del Volcán",
        inliCode = "Na-08",
        regionGroup = "Centro & Morelos",
        states = "Morelos (Tetela del Volcán, Hueyapan)",
        estimatedSpeakers = "6,000+ hablantes",
        status = "En uso",
        greetingPhrase = "Niltze, chicahuac tlazohcamati",
        greetingSpanish = "Hola, muchas gracias",
        linguisticTrait = "Vocabulario textil tradicional de gabanes de lana teñidos.",
        description = "Comunidad artesana textil al pie del volcán Popocatépetl."
    ),
    NahuatlVariantInfo(
        id = "mex_morelos_ixtla",
        name = "Mexicano de Puente de Ixtla",
        inliCode = "Na-03",
        regionGroup = "Centro & Morelos",
        states = "Morelos (Puente de Ixtla, Xoxocotla)",
        estimatedSpeakers = "12,000+ hablantes",
        status = "Vigorosa",
        greetingPhrase = "Niltze, ¿queniuhqui ticatej?",
        greetingSpanish = "Hola, ¿cómo están?",
        linguisticTrait = "Variante cálida de los valles del sur de Morelos.",
        description = "Comunidades indígenas históricas de Xoxocotla."
    ),
    NahuatlVariantInfo(
        id = "mex_centro_alto",
        name = "Mexicano del centro alto",
        inliCode = "Na-04",
        regionGroup = "Centro & Morelos",
        states = "Estado de México (Amecameca, Texcoco)",
        estimatedSpeakers = "15,000+ hablantes",
        status = "En riesgo medio",
        greetingPhrase = "Cualli yohualli, ¿ken timoyetzticah?",
        greetingSpanish = "Buenas noches, ¿cómo se encuentra?",
        linguisticTrait = "Influencia Nezahualcóyotl con elevado contenido lírico.",
        description = "Cuna del Rey Poeta Nezahualcóyotl en Texcoco."
    ),
    NahuatlVariantInfo(
        id = "na_oaxaca",
        name = "Náhuatl de Oaxaca",
        inliCode = "Na-17",
        regionGroup = "Oaxaca & Sur",
        states = "Oaxaca (Teotitlán del Camino, La Cañada)",
        estimatedSpeakers = "11,000+ hablantes",
        status = "Vigorosa",
        greetingPhrase = "Piyali, ¿ken tica tehua?",
        greetingSpanish = "Hola, ¿cómo estás?",
        linguisticTrait = "Fusión melódica con patrones tonales de la Cañada oaxaqueña.",
        description = "Unión entre los valles del sur de Puebla y el norte de Oaxaca."
    ),
    NahuatlVariantInfo(
        id = "na_puebla_oriente",
        name = "Mexicano del oriente de Puebla",
        inliCode = "Na-09",
        regionGroup = "Puebla & Sierra",
        states = "Puebla (Chilchotla, Tepanatepec)",
        estimatedSpeakers = "18,000+ hablantes",
        status = "Vigorosa",
        greetingPhrase = "Cualli tlaneci, ¿ken tica?",
        greetingSpanish = "Buenos días, ¿cómo estás?",
        linguisticTrait = "Combinación de formas de la Sierra con el altiplano oriental.",
        description = "Bosques de pino y encino en el oriente poblano."
    ),
    NahuatlVariantInfo(
        id = "na_puebla_pahuatlan",
        name = "Náhuatl alto del norte de Puebla",
        inliCode = "Na-07",
        regionGroup = "Puebla & Sierra",
        states = "Puebla (Pahuatlán, Naupan)",
        estimatedSpeakers = "28,000+ hablantes",
        status = "Vigorosa",
        greetingPhrase = "Piyali, ¿ken tinemi piltzintli?",
        greetingSpanish = "Hola, ¿cómo caminas, estimado?",
        linguisticTrait = "Asociada a la elaboración del papel amate sagrado.",
        description = "Pueblo mágico de Pahuatlán y rituales del papel amate."
    ),
    NahuatlVariantInfo(
        id = "mex_occidente_bajo",
        name = "Mexicano bajo de occidente",
        inliCode = "Na-05",
        regionGroup = "Guerrero & Occidente",
        states = "Michoacán (Aquila, Pomaro, Maruata)",
        estimatedSpeakers = "3,000+ hablantes",
        status = "En riesgo",
        greetingPhrase = "Nawene, ¿keniuka tica?",
        greetingSpanish = "Hola, ¿cómo estás?",
        linguisticTrait = "Sustituye 'TL' por 'L' en diversos contextos fonéticos.",
        description = "Costa de la Sierra Nahua de Michoacán frente al Océano Pacífico."
    ),
    NahuatlVariantInfo(
        id = "mex_occidente_central",
        name = "Mexicano central de occidente",
        inliCode = "Na-14",
        regionGroup = "Guerrero & Occidente",
        states = "Jalisco, Colima, Michoacán",
        estimatedSpeakers = "2,000+ hablantes",
        status = "En riesgo",
        greetingPhrase = "Nawene, yec tonalli",
        greetingSpanish = "Hola, buenos días",
        linguisticTrait = "Variante arcaica con rasgos dialectales únicos del poniente.",
        description = "Sierras del occidente mexicano."
    ),
    NahuatlVariantInfo(
        id = "mex_noroeste_durango",
        name = "Mexicano del noroeste",
        inliCode = "Na-10",
        regionGroup = "Guerrero & Occidente",
        states = "Durango (San Pedro de Xora)",
        estimatedSpeakers = "1,200+ hablantes",
        status = "En riesgo crítico / Aislada",
        greetingPhrase = "Nawene, cualli tonaltzin",
        greetingSpanish = "Hola, bien",
        linguisticTrait = "La comunidad náhuatl geográfica más distante al norte de México.",
        description = "Serranía de Durango junto a comunidades mexicanas y Tepehuanas."
    ),
    NahuatlVariantInfo(
        id = "mex_noroeste_central",
        name = "Mexicano del noroeste central",
        inliCode = "Na-21",
        regionGroup = "Guerrero & Occidente",
        states = "Jalisco (Tuxpan)",
        estimatedSpeakers = "1,500+ hablantes",
        status = "En riesgo / Preservación",
        greetingPhrase = "Nawene, ¿keniuka tinemi?",
        greetingSpanish = "Hola, ¿cómo estás?",
        linguisticTrait = "Pueblo de Tuxpan 'Pueblo de la Eterna Fiesta'.",
        description = "Faldas del Volcán de Fuego de Colima en Tuxpan, Jalisco."
    ),
    NahuatlVariantInfo(
        id = "mex_centro_bajo_edomex",
        name = "Mexicano central bajo",
        inliCode = "Na-11",
        regionGroup = "Centro & Morelos",
        states = "Estado de México (Malinalco, Tenancingo)",
        estimatedSpeakers = "5,000+ hablantes",
        status = "En riesgo",
        greetingPhrase = "Niltze, ¿ken techica?",
        greetingSpanish = "Hola, ¿cómo estás?",
        linguisticTrait = "Variante del hermoso valle de Malinalco.",
        description = "Santuario de guerreros águila y jaguar excavado en la roca."
    ),
    NahuatlVariantInfo(
        id = "mex_oriente_central",
        name = "Mexicano del oriente central",
        inliCode = "Na-16",
        regionGroup = "Puebla & Sierra",
        states = "Límites Puebla - Tlaxcala",
        estimatedSpeakers = "10,000+ hablantes",
        status = "En uso",
        greetingPhrase = "Niltze, ¿ken tinemij?",
        greetingSpanish = "Hola, ¿cómo estás?",
        linguisticTrait = "Paso natural entre los valles centrales y la vertiente del Golfo.",
        description = "Zona de transición agrícola y cultural."
    ),
    NahuatlVariantInfo(
        id = "na_tabasco",
        name = "Náhuatl de Tabasco / Chontal",
        inliCode = "Na-29",
        regionGroup = "Oaxaca & Sur",
        states = "Tabasco (Cupilco, Comalcalco)",
        estimatedSpeakers = "800+ hablantes",
        status = "En riesgo crítico",
        greetingPhrase = "Piyali, ¿ken timochihua?",
        greetingSpanish = "Hola, ¿cómo estás?",
        linguisticTrait = "Fuerte contacto histórico con hablantes de Chontal de Tabasco.",
        description = "Llanuras costeras del cacao y vestigios mayas de Comalcalco."
    ),
    NahuatlVariantInfo(
        id = "na_clasico_imperial",
        name = "Náhuatl Clásico Imperial",
        inliCode = "Na-30",
        regionGroup = "Centro & Morelos",
        states = "Tenochtitlan / Variante Académica Histórica",
        estimatedSpeakers = "Uso Educativo y Literario",
        status = "Histórico / Académico",
        greetingPhrase = "Niltze, ma cualli tonalli xicpiacan",
        greetingSpanish = "Saludos, Señor del cerca y del junto",
        linguisticTrait = "Estándar literario de los códices aztecas, gramáticas de Sahagún y Molina.",
        description = "El Náhuatl imperial de la Gran Tenochtitlan en el siglo XVI."
    )
)

/**
 * Main INPI-Style Interactive Modal Dialog for Náhuatl Variant Selection
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NahuatlVariantSelectionDialog(
    currentVariant: String,
    onVariantSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: com.example.ui.MainViewModel? = null
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf("Todas") }
    var selectedVariant by remember {
        mutableStateOf(
            NAHUATL_VARIANTS_CATALOG.firstOrNull { it.name == currentVariant }
                ?: NAHUATL_VARIANTS_CATALOG.first()
        )
    }
    var showInfographicSheet by remember { mutableStateOf(false) }

    val groupOptions = listOf(
        "Todas",
        "Huasteca",
        "Puebla & Sierra",
        "Veracruz & Istmo",
        "Centro & Morelos",
        "Guerrero & Occidente",
        "Oaxaca & Sur"
    )

    val filteredVariants = remember(searchQuery, selectedGroup) {
        NAHUATL_VARIANTS_CATALOG.filter { v ->
            val matchesGroup = selectedGroup == "Todas" || v.regionGroup == selectedGroup
            val matchesSearch = searchQuery.isBlank() ||
                    v.name.contains(searchQuery, ignoreCase = true) ||
                    v.states.contains(searchQuery, ignoreCase = true) ||
                    v.inliCode.contains(searchQuery, ignoreCase = true) ||
                    v.regionGroup.contains(searchQuery, ignoreCase = true)
            matchesGroup && matchesSearch
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .widthIn(max = 840.dp)
                    .fillMaxHeight(0.95f)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF7F5EE) // INPI warm cream backdrop
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                // --- Top Header Banner (Styled after INPI "Códice México" golden banner) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFF9A825), // Golden Amber
                                    Color(0xFFF57F17)  // Warm Gold
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🌿", fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Náhuatl (Na)",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "CATÁLOGO DE VARIANTES LINGÜÍSTICAS (INPI / INALI)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.9f),
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color.White
                            )
                        }
                    }
                }

                // --- Search & Region Filter Bar ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFEBE0))
                        .padding(12.dp)
                ) {
                    // Search Input Box
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Buscar por nombre o estado (ej. Huasteca, Veracruz, Puebla)...",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFF57F17))
                        },
                        trailingIcon = if (searchQuery.isNotEmpty()) {
                            {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpiar", tint = Color.Gray)
                                }
                            }
                        } else null,
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFFF9A825),
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Horizontal Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        groupOptions.forEach { group ->
                            val isSelected = selectedGroup == group
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedGroup = group },
                                label = {
                                    Text(
                                        text = group,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFF57F17),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White,
                                    labelColor = Color(0xFF333333)
                                )
                            )
                        }
                    }
                }

                // --- Flowing Grid of Variant Chips & Details (Scrollable Web-like Body) ---
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFF7F5EE))
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    if (filteredVariants.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔎", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No se encontraron variantes con ese término.",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        @OptIn(ExperimentalLayoutApi::class)
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filteredVariants.forEach { variant ->
                                val isSelected = selectedVariant.id == variant.id
                                val isCurrentActive = currentVariant == variant.name

                                Card(
                                    onClick = { selectedVariant = variant },
                                    modifier = Modifier
                                        .widthIn(min = 145.dp, max = 220.dp)
                                        .weight(1f, fill = false),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = when {
                                            isSelected -> Color(0xFFFFF8E1) // Warm gold tint
                                            else -> Color.White
                                        }
                                    ),
                                    border = BorderStroke(
                                        width = if (isSelected || isCurrentActive) 2.dp else 1.dp,
                                        color = when {
                                            isSelected -> Color(0xFFF57F17)
                                            isCurrentActive -> Color(0xFF4CAF50)
                                            else -> Color(0xFFE0E0E0)
                                        }
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = if (isSelected) 4.dp else 1.dp
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (isSelected) Color(0xFFF57F17) else Color(0xFFEEEEEE),
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            ) {
                                                Text(
                                                    text = variant.inliCode,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (isSelected) Color.White else Color(0xFF666666),
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }

                                            if (isCurrentActive) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = Color(0xFF4CAF50),
                                                    modifier = Modifier.size(16.dp)
                                                ) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = "Activa",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(11.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Text(
                                            text = variant.name,
                                            fontSize = 12.5.sp,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                            color = if (isSelected) Color(0xFFE65100) else Color(0xFF2C2C2C),
                                            textAlign = TextAlign.Center,
                                            lineHeight = 16.sp,
                                            maxLines = 2
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = variant.regionGroup,
                                            fontSize = 9.5.sp,
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // --- Selected Variant Detailed Info Card & Action ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFF57F17).copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedVariant.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFE65100)
                                    )
                                    Text(
                                        text = "📍 ${selectedVariant.states}",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFFFF3E0)
                                ) {
                                    Text(
                                        text = selectedVariant.estimatedSpeakers,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Greeting Example Pill
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFF8E1), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFFFE082), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("🗣️ ", fontSize = 16.sp)
                                        Column {
                                            Text(
                                                text = "\"${selectedVariant.greetingPhrase}\"",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF333333)
                                            )
                                            Text(
                                                text = "Traducción: ${selectedVariant.greetingSpanish}",
                                                fontSize = 11.5.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel?.speak(selectedVariant.greetingPhrase, selectedVariant.name)
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Escuchar pronunciación",
                                            tint = Color(0xFFE65100)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            val profile = viewModel?.getVariantPronunciationProfile(selectedVariant.name)
                            if (profile != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFE0F2F1), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = Color(0xFF00796B),
                                        modifier = Modifier.size(16.dp)
                                     )
                                     Spacer(modifier = Modifier.width(6.dp))
                                     Column {
                                         Text(
                                             text = "Ajuste Acústico: ${profile.phoneticLabel}",
                                             fontSize = 10.sp,
                                             fontWeight = FontWeight.Bold,
                                             color = Color(0xFF00796B)
                                         )
                                         Text(
                                             text = "Fono: Tono ${profile.pitch}x • Vel ${profile.speed}x • ${profile.description}",
                                             fontSize = 9.sp,
                                             color = Color(0xFF004D40)
                                         )
                                     }
                                 }
                             }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "💡 Rasgo: ${selectedVariant.linguisticTrait}",
                                fontSize = 11.5.sp,
                                color = Color(0xFF555555),
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action Button: Select Variant
                            Button(
                                onClick = {
                                    onVariantSelected(selectedVariant.name)
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF57F17)
                                )
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Activar la variante: ${selectedVariant.name}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // --- Bottom Bar with INPI "Infografía" Button (matching reference image) ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFEBE0))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { showInfographicSheet = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF9A825)
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Infografía",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "30 Variantes Registradas INPI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
        }
    }
    }

    if (showInfographicSheet) {
        NahuatlInfographicModal(onDismiss = { showInfographicSheet = false })
    }
}

/**
 * Fullscreen Interactive Infographic Sheet for Náhuatl Variants
 */
@Composable
fun NahuatlInfographicModal(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .widthIn(max = 840.dp)
                    .fillMaxHeight(0.95f)
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF12121A) // Deep cosmic dark for vibrant infographic
            ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Infographic Title Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📊 INFOGRAFÍA NACIONAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFB74D),
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Las 30 Variantes del Náhuatl en México",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stat Summary Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfographicStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Población Total",
                        value = "1.73 Millones",
                        subtitle = "de hablantes en México",
                        badgeColor = Color(0xFF84E02C)
                    )
                    InfographicStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Variantes INPI",
                        value = "30 Dialectos",
                        subtitle = "reconocidos oficialmente",
                        badgeColor = Color(0xFFFF9800)
                    )
                    InfographicStatCard(
                        modifier = Modifier.weight(1f),
                        title = "Estados",
                        value = "16 Entidades",
                        subtitle = "con presencia vibrante",
                        badgeColor = Color(0xFF2196F3)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Visual Sound Map Section: TL vs T vs L sound branches
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E28)),
                    border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🗣️ LAS TRES RAMAS SONORAS DEL NÁHUATL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFB74D)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Linguísticamente, las variantes se agrupan según cómo pronuncian la consonante ancestral del grupo consonántico:",
                            fontSize = 11.5.sp,
                            color = Color.LightGray,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        BranchRow(
                            sound = "TL",
                            label = "Náhuatl con TL (Altiplano / Huasteca)",
                            example = "Atl (Agua) • Tlacatl (Hombre) • Tonaltzin (Sol)",
                            color = Color(0xFF84E02C)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        BranchRow(
                            sound = "T",
                            label = "Nawat con T (Istmo de Veracruz)",
                            example = "At (Agua) • Takat (Hombre) • Tonatsin (Sol)",
                            color = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        BranchRow(
                            sound = "L",
                            label = "Nahual con L (Occidente de Michoacán)",
                            example = "Al (Agua) • Lacal (Hombre) • Lonal (Sol)",
                            color = Color(0xFFE91E63)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Comparative Phrase Table across Major Regions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E28)),
                    border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💬 TABLA COMPARATIVA DE SALUDOS POR REGIÓN",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        ComparisonTableRow(
                            region = "Huasteca (Hidalgo/Veracruz)",
                            phrase = "Piltzin, ¿kenke ticate?",
                            spanish = "Hola, ¿cómo estás?",
                            accentColor = Color(0xFFFFB74D)
                        )
                        Divider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 6.dp))

                        ComparisonTableRow(
                            region = "Sierra de Cuetzalan (Puebla)",
                            phrase = "Piyali, ¿ken tinemi?",
                            spanish = "Hola, ¿cómo estás?",
                            accentColor = Color(0xFFC6FF00)
                        )
                        Divider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 6.dp))

                        ComparisonTableRow(
                            region = "Guerrero (Chilapa/Olinalá)",
                            phrase = "Piyali, ¿ken tinemi?",
                            spanish = "Hola, ¿cómo caminas?",
                            accentColor = Color(0xFF64B5F6)
                        )
                        Divider(color = Color.Gray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 6.dp))

                        ComparisonTableRow(
                            region = "Altiplano Central (Tenochtitlan/CDMX)",
                            phrase = "Niltze, cualli tonalli",
                            spanish = "Hola, buenos días",
                            accentColor = Color(0xFFBA68C8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D))
                ) {
                    Text(
                        text = "Entendido • Volver al Catálogo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
}

@Composable
private fun InfographicStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    badgeColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E28)),
        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 9.5.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 12.5.sp, fontWeight = FontWeight.Black, color = badgeColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 8.5.sp, color = Color.LightGray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun BranchRow(
    sound: String,
    label: String,
    example: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = color,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(sound, fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(example, fontSize = 10.sp, color = Color.LightGray)
        }
    }
}

@Composable
private fun ComparisonTableRow(
    region: String,
    phrase: String,
    spanish: String,
    accentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(region, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = accentColor)
            Text("\"$phrase\"", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Text(spanish, fontSize = 10.5.sp, color = Color.LightGray)
    }
}
