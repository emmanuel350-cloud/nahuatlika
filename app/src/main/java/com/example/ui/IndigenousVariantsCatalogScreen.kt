package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class IndigenousVariantItem(
    val id: String,
    val name: String,
    val code: String,
    val region: String,
    val states: String,
    val speakers: String,
    val greeting: String,
    val greetingSpanish: String,
    val trait: String,
    val description: String
)

// --- MAYA VARIANTS ---
val MAYA_VARIANTS = listOf(
    IndigenousVariantItem(
        id = "my_yucateco",
        name = "Maya Yucateco (Maayat'aan)",
        code = "My-01",
        region = "Península",
        states = "Yucatán, Quintana Roo, Campeche",
        speakers = "800,000+ hablantes",
        greeting = "Ba'ax ka wa'alik, ¿bix a beel?",
        greetingSpanish = "Hola, ¿cómo va tu camino?",
        trait = "Tono musical glotalizado, uso extenso de sufijo -al y fonémica de vocales rearticuladas.",
        description = "La gran variante de las ciudades sagradas de Chichén Itzá, Uxmal y Tulum."
    ),
    IndigenousVariantItem(
        id = "my_chol",
        name = "Ch'ol (Ch'ol-ti')",
        code = "My-02",
        region = "Selva Norte",
        states = "Chiapas (Palenque, Tila, Tumbalá), Tabasco",
        speakers = "250,000+ hablantes",
        greeting = "K'otik, ¿bajche' awilal?",
        greetingSpanish = "Hola, ¿cómo se ve tu día?",
        trait = "Vigilante guardián del sitio de Palenque y las cascadas de Agua Azul.",
        description = "Materia primordial en las inscripciones glíficas de Pakal el Grande."
    ),
    IndigenousVariantItem(
        id = "my_tzotzil",
        name = "Tzotzil (Bats'i k'op)",
        code = "My-03",
        region = "Los Altos",
        states = "Chiapas (San Cristóbal, Zinacantán, Chamula)",
        speakers = "550,000+ hablantes",
        greeting = "K'uxi, ¿k'u x'elan oyot?",
        greetingSpanish = "Hola, ¿cómo te encuentras?",
        trait = "BATS'I K'OP ('Palabra Verdadera'). Expresividad poética de la indumentaria de lana y telar.",
        description = "Gente de los murciélagos y guardianes sagrados de San Juan Chamula."
    ),
    IndigenousVariantItem(
        id = "my_tzeltal",
        name = "Tzeltal (Batal k'op)",
        code = "My-04",
        region = "Selva & Altos",
        states = "Chiapas (Ocosingo, Tenejapa, Oxchuc)",
        speakers = "590,000+ hablantes",
        greeting = "Lek'bal, ¿bintil ayat?",
        greetingSpanish = "Hola, ¿cómo estás hoy?",
        trait = "Cultura viva de la milpa sustentable, la miel silvestre de melipona y la música de arpa.",
        description = "Una de las variantes mayas más extendidas de los valles chiapanecos."
    ),
    IndigenousVariantItem(
        id = "my_tojolabal",
        name = "Tojolabal (Tojol k'op)",
        code = "My-05",
        region = "Soconusco y Frontera",
        states = "Chiapas (Las Margaritas, Comitán, Altamirano)",
        speakers = "65,000+ hablantes",
        greeting = "Lajal, ¿jas mawa'alik?",
        greetingSpanish = "Hola, ¿qué dicen?",
        trait = "Filosofía comunitaria del nosotros ('Altanil') y fuerte estructura morfológica.",
        description = "Comunidad viva de la selva lacandona y la frontera sur con Guatemala."
    ),
    IndigenousVariantItem(
        id = "my_lacandon",
        name = "Lacandón (Hach t'an)",
        code = "My-06",
        region = "Selva Lacandona",
        states = "Chiapas (Lacanjá Chansayab, Naha)",
        speakers = "1,000+ hablantes",
        greeting = "Kimi, ¿bix yanikech?",
        greetingSpanish = "Hola, ¿cómo te encuentras?",
        trait = "Conserva las tradiciones y rituales más antiguos del contacto maya prehispánico.",
        description = "Cuidadores ancestrales de la gran Selva Lacandona y los jaguares salvajes."
    ),
    IndigenousVariantItem(
        id = "my_huasteco",
        name = "Huasteco (Téenek)",
        code = "My-07",
        region = "Huasteca Potosina",
        states = "San Luis Potosí, Veracruz, Tamaulipas",
        speakers = "170,000+ hablantes",
        greeting = "Jajax, ¿it k'wajat t'ajbil?",
        greetingSpanish = "Hola, ¿cómo estás haciendo?",
        trait = "Rama más septentrional de la familia maya separada geográficamente del tronco principal.",
        description = "Gente de la Huasteca, célebres por su exquisita alfarería y bordados florales estrella."
    )
)

// --- ZAPOTECO VARIANTS ---
val ZAPOTECO_VARIANTS = listOf(
    IndigenousVariantItem(
        id = "zap_istmo",
        name = "Zapoteco del Istmo (Diidxazá)",
        code = "Za-01",
        region = "Istmo de Tehuantepec",
        states = "Oaxaca (Juchitán, Tehuantepec, Espinal, Ixtepec)",
        speakers = "120,000+ hablantes",
        greeting = "Padiush, ¿bixaza?",
        greetingSpanish = "Salud divina, ¿cómo estás?",
        trait = "Lengua tonal de gran lirismo, velas tradicionales, huipiles de bordado y poesía viva.",
        description = "Cuna de Andrés Henestrosa, sones istmeños y la tradición de la Sandunga."
    ),
    IndigenousVariantItem(
        id = "zap_valles",
        name = "Zapoteco de Valles Centrales (Didxazə)",
        code = "Za-02",
        region = "Valles Centrales",
        states = "Oaxaca (Zaachila, Mitla, Ocotlán, Tlacolula)",
        speakers = "100,000+ hablantes",
        greeting = "Padiush zne, ¿bixaza re'?",
        greetingSpanish = "Hola, ¿cómo estás por aquí?",
        trait = "Variante directa del centro ceremonial de Monte Albán y la greca de piedra de Mitla.",
        description = "Gente de las nubes ('Ben 'Zaa') y tejedores de tapetes de lana con tintes naturales."
    ),
    IndigenousVariantItem(
        id = "zap_sierra_norte",
        name = "Zapoteco de la Sierra Norte (Diza Xhon)",
        code = "Za-03",
        region = "Sierra Juárez",
        states = "Oaxaca (Ixtlán de Juárez, Villa Alta, Guelatao)",
        speakers = "85,000+ hablantes",
        greeting = "Gari, ¿bi'lla' za?",
        greetingSpanish = "Hola hermano, ¿cómo andas?",
        trait = "Tierra natal de Benito Juárez. Gobernanza comunitaria por usos y costumbres.",
        description = "Bosques de matorral sagrado, bandas de viento ancestrales y herbolaria de montaña."
    ),
    IndigenousVariantItem(
        id = "zap_sierra_sur",
        name = "Zapoteco de la Sierra Sur (Di'zhza'a)",
        code = "Za-04",
        region = "Sierra Sur",
        states = "Oaxaca (Miahuatlán, Yautepec, Sola de Vega)",
        speakers = "60,000+ hablantes",
        greeting = "Padiux, ¿ti'zhxa ya'?",
        greetingSpanish = "Hola, ¿cómo está la palabra?",
        trait = "Estructura tonal compleja con consonantes aspiradas, glotales y fortis.",
        description = "Hermosas montañas de la Sierra Sur, productoras de café fino de sombra y mezcal silvestre."
    ),
    IndigenousVariantItem(
        id = "zap_costa",
        name = "Zapoteco de la Costa (Dizhsa)",
        code = "Za-05",
        region = "Costa de Oaxaca",
        states = "Oaxaca (Pochutla, Tonameca, Tututepec)",
        speakers = "25,000+ hablantes",
        greeting = "Padiux, ¿xa naca re'?",
        greetingSpanish = "Hola, ¿cómo están por aquí?",
        trait = "Variante costera que posee un fuerte contacto histórico con las lenguas de la región baja.",
        description = "Valles bajos fértiles y hermosos litorales pacíficos con gran biodiversidad de flora."
    )
)

// --- MIXTECO VARIANTS ---
val MIXTECO_VARIANTS = listOf(
    IndigenousVariantItem(
        id = "mix_alta",
        name = "Mixteco de la Mixteca Alta (Tu'un Savi)",
        code = "Mx-01",
        region = "Mixteca Alta",
        states = "Oaxaca (Tlaxiaco, Yanhuitlán, Teposcolula, Huajuapan)",
        speakers = "230,000+ hablantes",
        greeting = "Tanitazü, ¿saviaa?",
        greetingSpanish = "Bendición de lluvia, ¿cómo estás?",
        trait = "TU'UN SAVI ('Palabra de la Lluvia'). Códices prehispánicos como el Codex Nuttall.",
        description = "Maestros del labrado de turquesa, oro y templos monumentales dominicos."
    ),
    IndigenousVariantItem(
        id = "mix_baja",
        name = "Mixteco de la Mixteca Baja (To'on Savi)",
        code = "Mx-02",
        region = "Mixteca Baja",
        states = "Oaxaca, Puebla (Juxtlahuaca, Silacayoapam, Acatlán)",
        speakers = "180,000+ hablantes",
        greeting = "Kua'an vi'i, ¿saviaa nda'a?",
        greetingSpanish = "Buenos días, ¿cómo va todo allá?",
        trait = "Rica tradición de la Danza de los Tecuanes y el cultivo sustentable del pitayo.",
        description = "Tierra fértil de artesanos de la palma, alfarería roja y jarabes de la culebra."
    ),
    IndigenousVariantItem(
        id = "mix_costa",
        name = "Mixteco de la Costa (Tnu'un Savi)",
        code = "Mx-03",
        region = "Costa Chica",
        states = "Oaxaca, Guerrero (Jamiltepec, Pinotepa Nacional, Metlatónoc)",
        speakers = "110,000+ hablantes",
        greeting = "Tanita, ¿saviaa tnu'un?",
        greetingSpanish = "Hola, ¿cómo está la palabra?",
        trait = "Tejido en telar de cintura con caracol púrpura pansa y chile costeño.",
        description = "Encuentro vibrante de la Costa Chica, chilenas y danza de los diablos."
    ),
    IndigenousVariantItem(
        id = "mix_guerrero",
        name = "Mixteco de Guerrero (Tu'un Savi)",
        code = "Mx-04",
        region = "La Montaña de Guerrero",
        states = "Guerrero (Metlatónoc, Cochoapa el Grande, Alcozauca)",
        speakers = "140,000+ hablantes",
        greeting = "Tanitani, ¿saviaa ndu'u?",
        greetingSpanish = "Hola, ¿cómo está nuestra lluvia?",
        trait = "Una de las variantes lingüísticas más vigorosas de la Montaña; conserva bellos arcaísmos fonéticos.",
        description = "Comunidades de la alta montaña escarpada de Guerrero con fuertes asambleas comunitarias."
    ),
    IndigenousVariantItem(
        id = "mix_puebla",
        name = "Mixteco de Puebla (Da'an Davi)",
        code = "Mx-05",
        region = "Mixteca Poblana",
        states = "Puebla (Acatlán, Piaxtla, Tecomatlán)",
        speakers = "10,000+ hablantes",
        greeting = "Tanitazü, ¿saviaa ree?",
        greetingSpanish = "Hola, ¿cómo estás tú?",
        trait = "Variante septentrional de la gran familia Mixteca con una fonología sumamente suave.",
        description = "Zonas semiáridas poblanas muy famosas por sus obras de barro policromado y tejido de palma."
    )
)

// --- OTOMÍ VARIANTS ---
val OTOMI_VARIANTS = listOf(
    IndigenousVariantItem(
        id = "oto_mezquital",
        name = "Otomí del Valle del Mezquital (Hñähñu)",
        code = "Ot-01",
        region = "Valle del Mezquital",
        states = "Hidalgo (Ixmiquilpan, Actopan, Cardonal, Chilcuautla)",
        speakers = "115,000+ hablantes",
        greeting = "Ki rät'i, ¿hanza te k'o?",
        greetingSpanish = "Hola, ¿cómo va tu ser?",
        trait = "HÑÄHÑU ('Hablantes de la lengua silbada con la garganta'). Cultura del sagrado Maguey.",
        description = "Bebida de los dioses (Pulque), bordados geométricos y cocina de flor de gualumbo."
    ),
    IndigenousVariantItem(
        id = "oto_temoaya",
        name = "Otomí de Temoaya (Ñätho)",
        code = "Ot-02",
        region = "Alto Lerma",
        states = "Estado de México (Temoaya, Otzolotepec, Toluca, Jiquipilco)",
        speakers = "90,000+ hablantes",
        greeting = "Ki xudi, ¿hanza te pfe?",
        greetingSpanish = "Buenos días, ¿cómo va tu trabajo?",
        trait = "Centro Ceremonial Otomí de Temoaya y tapetes anudados a mano de lana pura.",
        description = "Guardianes del bosque sagrado de las mariposas monarca y arroyos de agua limpia."
    ),
    IndigenousVariantItem(
        id = "oto_tenango",
        name = "Otomí de la Sierra / Tenango (Hñähñö)",
        code = "Ot-03",
        region = "Sierra Oriental",
        states = "Hidalgo (Tenango de Doria, San Bartolo Tutotepec), Puebla",
        speakers = "75,000+ hablantes",
        greeting = "Ki xudi, ¿hanza te hñähño?",
        greetingSpanish = "Buenos días, ¿cómo estás hoy?",
        trait = "Creadores de los mundialmente famosos TENANGOS: lienzos bordados multicolor con fauna y flora míticas.",
        description = "Cien gallos, venados de fuego y flores sagradas plasmados en bordados colectivos."
    ),
    IndigenousVariantItem(
        id = "oto_queretaro",
        name = "Otomí de Querétaro (Ñäñho)",
        code = "Ot-04",
        region = "Semidesierto",
        states = "Querétaro (Amealco de Bonfil, Tolimán, Cadereyta)",
        speakers = "35,000+ hablantes",
        greeting = "Ki hats'i, ¿hanza te b'u'i?",
        greetingSpanish = "Buenos días, ¿cómo vives?",
        trait = "Famosos internacionalmente por la confección de la hermosa muñeca artesanal sagrada 'Lelé'.",
        description = "Tierra semidesértica de capillas oratorio familiares e hilos de herencia milenaria viva."
    ),
    IndigenousVariantItem(
        id = "oto_ixtenco",
        name = "Otomí de Ixtenco (Yümu)",
        code = "Ot-05",
        region = "Altiplano",
        states = "Tlaxcala (San Juan Ixtenco)",
        speakers = "1,500+ hablantes",
        greeting = "Ki hats'i, ¿hanza te n'u'i?",
        greetingSpanish = "Buenos días, ¿cómo andas tú?",
        trait = "La variante más oriental del otomí, caracterizada por magníficos bordados de pepenado e hilados.",
        description = "Comunidad tlaxcalteca que protege celosamente variedades únicas de maíces nativos de colores."
    )
)

// --- COMPONENT: MAYA VARIANT DIALOG ---
@Composable
fun MayaVariantSelectionDialog(
    currentVariant: String,
    onVariantSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: com.example.ui.MainViewModel? = null
) {
    GenericVariantDialog(
        title = "🌅 Catálogo INPI: Variantes Mayas",
        subtitle = "Selecciona la variante regional de la gran familia Mayance:",
        items = MAYA_VARIANTS,
        currentVariant = currentVariant,
        accentColor = Color(0xFF00796B),
        onVariantSelected = onVariantSelected,
        onDismiss = onDismiss,
        viewModel = viewModel
    )
}

// --- COMPONENT: ZAPOTECO VARIANT DIALOG ---
@Composable
fun ZapotecoVariantSelectionDialog(
    currentVariant: String,
    onVariantSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: com.example.ui.MainViewModel? = null
) {
    GenericVariantDialog(
        title = "🏔️ Catálogo INPI: Variantes Zapotecas",
        subtitle = "Selecciona la variante regional de la gente de las nubes (Ben 'Zaa):",
        items = ZAPOTECO_VARIANTS,
        currentVariant = currentVariant,
        accentColor = Color(0xFF1565C0),
        onVariantSelected = onVariantSelected,
        onDismiss = onDismiss,
        viewModel = viewModel
    )
}

// --- COMPONENT: MIXTECO VARIANT DIALOG ---
@Composable
fun MixtecoVariantSelectionDialog(
    currentVariant: String,
    onVariantSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: com.example.ui.MainViewModel? = null
) {
    GenericVariantDialog(
        title = "🏹 Catálogo INPI: Variantes Mixtecas",
        subtitle = "Selecciona la variante regional del Pueblo de la Lluvia (Tu'un Savi):",
        items = MIXTECO_VARIANTS,
        currentVariant = currentVariant,
        accentColor = Color(0xFF6A1B9A),
        onVariantSelected = onVariantSelected,
        onDismiss = onDismiss,
        viewModel = viewModel
    )
}

// --- COMPONENT: OTOMÍ VARIANT DIALOG ---
@Composable
fun OtomiVariantSelectionDialog(
    currentVariant: String,
    onVariantSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: com.example.ui.MainViewModel? = null
) {
    GenericVariantDialog(
        title = "🌸 Catálogo INPI: Variantes Otomíes",
        subtitle = "Selecciona la variante regional del pueblo Hñähñu / Ñätho:",
        items = OTOMI_VARIANTS,
        currentVariant = currentVariant,
        accentColor = Color(0xFFC2185B),
        onVariantSelected = onVariantSelected,
        onDismiss = onDismiss,
        viewModel = viewModel
    )
}

// --- REUSABLE VARIANT DIALOG ---
@Composable
fun GenericVariantDialog(
    title: String,
    subtitle: String,
    items: List<IndigenousVariantItem>,
    currentVariant: String,
    accentColor: Color,
    onVariantSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: com.example.ui.MainViewModel? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp)),
            color = Color(0xFF121418),
            border = BorderStroke(2.dp, accentColor.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = accentColor
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(items) { item ->
                        val isSelected = currentVariant == item.name
                        Card(
                            onClick = {
                                onVariantSelected(item.name)
                                onDismiss()
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) accentColor.copy(alpha = 0.22f) else Color(0xFF1E222A)
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) accentColor else Color.Gray.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color.LightGray
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(accentColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(item.code, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = accentColor)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text("📍 ${item.states}", fontSize = 10.sp, color = Color.Gray)
                                Text("👥 ${item.speakers}", fontSize = 10.sp, color = accentColor, fontWeight = FontWeight.Bold)

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("🗣️ Saludo: ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = accentColor)
                                        Text("\"${item.greeting}\"", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel?.speak(item.greeting, item.name)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Escuchar pronunciación",
                                            tint = accentColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                val profile = viewModel?.getVariantPronunciationProfile(item.name)
                                if (profile != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                                            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.VolumeUp,
                                            contentDescription = null,
                                            tint = accentColor,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Ajuste: ${profile.phoneticLabel} (Tono: ${profile.pitch}x • Vel: ${profile.speed}x)",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(item.description, fontSize = 11.sp, color = Color.LightGray.copy(alpha = 0.85f), lineHeight = 15.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Aceptar y Guardar Variante", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- COMPONENT: ANCESTRAL INFOGRAPHIC DIALOG ---
@Composable
fun LanguageInfographicDialog(
    language: String,
    onDismiss: () -> Unit
) {
    val (title, icon, headerBg, accentCol, region, place, speakers, treeVariants, facts, cheatSheet) = when (language) {
        "Totona" -> Tuple10(
            "XANAT (LENGUA TOTONACA)",
            "🦜",
            Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFF3E2723))),
            Color(0xFFFFCC80),
            "Totonacapan (Veracruz y Sierra de Puebla)",
            "El Tajín, Papantla, Cempoala, Cuetzalan",
            "260,000+ hablantes activos",
            listOf("Costa (Papantla)", "Norte", "Sierra Sur"),
            listOf(
                "🏛️ Pirámide de los Nichos en El Tajín: Obra arquitectónica de 365 nichos alineados exactamente con el calendario solar.",
                "🌿 La Flor Negra de la Vainilla (Xanāt): Leyenda del amor prohibido de la princesa Tzacopontziza convertido en orquídea perfumada.",
                "🦅 Ritual Sagrado de los Voladores: 5 hombres pájaro descendiendo desde un mástil de 30 metros pidiendo lluvia a los 4 puntos cardinales.",
                "🎶 Lengua Canto de Pájaro: Estructura fonética rica con vocales laríngeas y gran expresividad poética marítima."
            ),
            listOf(
                Pair("Hola", "Tlan tlan"),
                Pair("Gracias", "Paxcatgatzilh"),
                Pair("Sol", "Chichini"),
                Pair("Luna", "Papak"),
                Pair("Agua", "Chuchut")
            )
        )
        else -> Tuple10(
            "MEXICATL (LENGUA NÁHUATL)",
            "🌿",
            Brush.verticalGradient(listOf(Color(0xFF33691E), Color(0xFF1B3300))),
            Color(0xFFC6FF00),
            "Anáhuac, Puebla, Veracruz, Hidalgo, Guerrero",
            "Templo Mayor, Tenochtitlan, Teotihuacán, Cholula",
            "1,700,000+ hablantes activos",
            listOf("Huasteca", "Central", "Guerrero", "Sierra de Puebla", "Istmo"),
            listOf(
                "🏛️ Gran Imperio Azteca & Tenochtitlan: La deslumbrante metrópoli lacustre fundada en 1325 sobre el islote del águila y el nopal.",
                "✍️ Tlacuilos & Escritura Amate: Sabios pintores de códices que registraban astronomía, medicina botánica, tributos e historia.",
                "🌺 Flor y Canto (In Xochitl In Cuicatl): La alta filosofía nahua expresada por reyes poetas como Nezahualcóyotl de Texcoco.",
                " Piedra del Sol / Monolito del Calendario: Escultura cósmica de 24 toneladas que resume los cinco soles y las eras de la tierra."
            ),
            listOf(
                Pair("Hola", "Niltze"),
                Pair("Gracias", "Tlazohcamati"),
                Pair("Sol", "Tonatiuh"),
                Pair("Luna", "Metztli"),
                Pair("Agua", "Atl")
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(26.dp)),
            color = Color(0xFF0E1014),
            border = BorderStroke(2.dp, accentCol.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerBg, RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(icon, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("📍 Región: $region", fontSize = 11.sp, color = accentCol, fontWeight = FontWeight.Bold)
                        Text("🏛️ Sitios Emblemáticos: $place", fontSize = 11.sp, color = Color.LightGray)
                        Text("👥 Hablantes: $speakers", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Regional Variants Tree
                Text(
                    text = "🌿 ÁRBOL LINGÜÍSTICO Y VARIANTES REGIONALES:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentCol
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    treeVariants.forEach { variantName ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E222A)),
                            border = BorderStroke(1.dp, accentCol.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "🔹 $variantName",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cultural Facts Cards
                Text(
                    text = "📜 CONOCIMIENTOS ANCESTRALES E INFOGRAFÍA:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentCol
                )
                Spacer(modifier = Modifier.height(8.dp))

                facts.forEach { fact ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161920)),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.25f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = fact,
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vocab Cheat Sheet
                Text(
                    text = "🗣️ VOCABULARIO CLAVE DE MUESTRA:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentCol
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1D24)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        cheatSheet.forEach { (spanish, native) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(spanish, fontSize = 12.sp, color = Color.Gray)
                                Text(native, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = accentCol)
                            }
                            Divider(color = Color.Gray.copy(alpha = 0.15f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = accentCol),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("¡Entendido! Explorar Lecciones 🚀", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

data class Tuple10<A, B, C, D, E, F, G, H, I, J>(
    val val1: A, val val2: B, val val3: C, val val4: D, val val5: E,
    val val6: F, val val7: G, val val8: H, val val9: I, val val10: J
)
