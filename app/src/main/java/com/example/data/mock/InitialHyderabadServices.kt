package com.example.data.mock

import com.example.data.model.Coupon
import com.example.data.model.ProfessionalInfo
import com.example.data.model.ServiceAddOn
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceItem
import com.example.data.model.ServiceVariant

object InitialHyderabadServices {

    val HYDERABAD_AREAS = listOf(
        "Madhapur, Hyderabad 500081",
        "Hitec City, Hyderabad 500081",
        "Gachibowli, Hyderabad 500032",
        "Jubilee Hills, Hyderabad 500033",
        "Banjara Hills, Hyderabad 500034",
        "Kondapur, Hyderabad 500084",
        "Kukatpally, Hyderabad 500072",
        "Miyapur, Hyderabad 500049",
        "Begumpet, Hyderabad 500016",
        "Secunderabad, Hyderabad 500003"
    )

    val CATEGORIES = listOf(
        ServiceCategory("cleaning", "Cleaning", "cleaning_services", "Deep cleaning, sanitization & scrubbing", isPopular = true, serviceCount = 5),
        ServiceCategory("ac_service", "AC Service", "hvac", "Jet pump servicing, gas recharge & repair", isPopular = true, serviceCount = 4),
        ServiceCategory("electrical", "Electrical", "electrical_services", "Fans, wiring, switches & emergency fix", isPopular = true, serviceCount = 4),
        ServiceCategory("plumbing", "Plumbing", "plumbing", "Taps, mixer, leakages & tank clean", isPopular = true, serviceCount = 4),
        ServiceCategory("appliances", "Appliances", "kitchen", "Washing machines, fridge, microwave & RO", isPopular = false, serviceCount = 4),
        ServiceCategory("painting", "Painting", "format_paint", "Interior walls, textures & waterproofing", isPopular = false, serviceCount = 3),
        ServiceCategory("pest_control", "Pest Control", "pest_control", "Cockroaches, bed bugs, herbal shield", isPopular = false, serviceCount = 3),
        ServiceCategory("carpentry", "Carpentry", "carpenter", "Locks, hinges, furniture assembly", isPopular = false, serviceCount = 3)
    )

    val SERVICES = listOf(
        ServiceItem(
            id = "clean_full_home",
            categoryId = "cleaning",
            title = "Full Home Deep Cleaning",
            shortDesc = "Intense mechanized cleaning of all rooms, kitchen, balconies & bathrooms",
            startingPrice = 1899,
            durationMins = 240,
            rating = 4.88,
            reviewCount = 1420,
            whatIsIncluded = listOf(
                "Floor scrubbing with single-disc scrubbing machine",
                "Deep sanitization and descaling of all bathrooms & tiles",
                "Kitchen degreasing of counters, cabinets, sink & tiles",
                "Balcony wash and dry vacuuming of curtains & sofas",
                "Mirror, glass window and door frame polish"
            ),
            whatIsNotIncluded = listOf(
                "Terrace or outer building facade cleaning",
                "Appliance internal electronic repairs",
                "Removal of wet paint or cement stains from construction"
            ),
            faqs = listOf(
                "Do I need to provide cleaning chemicals?" to "No, our verified pros bring complete professional Diversey/Taski industrial solutions.",
                "How many pros will visit?" to "A crew of 2 to 3 trained technicians with industrial vacuums and polishers."
            ),
            variants = listOf(
                ServiceVariant("clean_1bhk", "1 BHK Deep Clean", "Full compact home scrubbing up to 600 sq ft", 1899, 180),
                ServiceVariant("clean_2bhk", "2 BHK Deep Clean", "Standard 2BHK flat deep cleaning up to 1100 sq ft", 2499, 240),
                ServiceVariant("clean_3bhk", "3 BHK Deep Clean", "Large 3BHK deep cleaning up to 1600 sq ft", 3299, 300),
                ServiceVariant("clean_villa", "4 BHK / Villa Deep Clean", "Extensive villa deep scrub up to 2500 sq ft", 4499, 360)
            ),
            addOns = listOf(
                ServiceAddOn("addon_sofa", "Sofa Shampooing (3 Seater)", 549, "Deep foam extraction and sanitization"),
                ServiceAddOn("addon_fridge", "Refrigerator Interior Scrub", 299, "Antibacterial steam clean of trays"),
                ServiceAddOn("addon_mattress", "King Mattress Sanitization", 499, "Dust mite extraction & ultraviolet shield")
            )
        ),
        ServiceItem(
            id = "clean_bathroom",
            categoryId = "cleaning",
            title = "Bathroom Deep Descaling & Clean",
            shortDesc = "Hard-water scale removal from tiles, taps, mirrors & commode",
            startingPrice = 499,
            durationMins = 60,
            rating = 4.82,
            reviewCount = 2890,
            whatIsIncluded = listOf(
                "Tile scrubbing and grout line stain removal",
                "Mirror and glass partition streak-free wipe",
                "Descaling taps, faucets, shower heads and chrome accessories",
                "Thorough disinfection of toilet pot, washbasin & drains"
            ),
            whatIsNotIncluded = listOf(
                "Grout replacement or tile re-cementing",
                "Plumbing pipe replacement"
            ),
            faqs = listOf(
                "Does this remove yellow hard-water deposits?" to "Yes, our specialized acidic scale dissolver removes up to 95% of mineral scaling."
            ),
            variants = listOf(
                ServiceVariant("bath_1", "1 Bathroom Clean", "Comprehensive scrubbing of 1 bathroom", 499, 60),
                ServiceVariant("bath_2", "2 Bathrooms Clean", "Combo pack for 2 bathrooms (Save ₹100)", 899, 110),
                ServiceVariant("bath_3", "3 Bathrooms Clean", "Full home 3 bathrooms pack (Save ₹200)", 1299, 160)
            ),
            addOns = listOf(
                ServiceAddOn("addon_drain", "Enzymatic Drain Unclogger", 199, "Dissolves hair, grease & eliminates odor"),
                ServiceAddOn("addon_exhaust", "Exhaust Fan Scrub", 149, "Grease wipe & motor outer clean")
            )
        ),
        ServiceItem(
            id = "ac_jet_wash",
            categoryId = "ac_service",
            title = "PowerJet AC Servicing",
            shortDesc = "High-pressure water pump coil cleaning, filter wash & cooling check",
            startingPrice = 499,
            durationMins = 45,
            rating = 4.91,
            reviewCount = 3710,
            whatIsIncluded = listOf(
                "Indoor cooling coil wash with high-pressure PowerJet spray",
                "Outdoor condenser unit dust & debris wash",
                "Drain pipe clearing and leak test",
                "Amperage, gas pressure and cooling airflow test"
            ),
            whatIsNotIncluded = listOf(
                "Refrigerant gas top-up or leakage fixing",
                "PCB circuit replacement"
            ),
            faqs = listOf(
                "Will water spill on walls or furniture?" to "No, we use an all-around waterproof jacket funnel during the jet wash."
            ),
            variants = listOf(
                ServiceVariant("ac_1_split", "1x Split AC Jet Service", "Complete indoor and outdoor powerjet service", 499, 45),
                ServiceVariant("ac_2_split", "2x Split AC Combo", "Servicing for 2 Split ACs (Save ₹100)", 899, 80),
                ServiceVariant("ac_1_window", "1x Window AC Service", "Window AC pull-out cleaning & coil scrub", 449, 45)
            ),
            addOns = listOf(
                ServiceAddOn("addon_ac_antirust", "Anti-Rust Protective Coating", 299, "Prevents coil corrosion from coastal/saline air"),
                ServiceAddOn("addon_ac_foam", "Active Enzyme Foam Shield", 199, "Eliminates bacteria & mildew odors")
            )
        ),
        ServiceItem(
            id = "ac_gas_recharge",
            categoryId = "ac_service",
            title = "AC Gas Leak Fix & Refill",
            shortDesc = "Nitrogen pressure leak detection, brazing & full refrigerant recharge",
            startingPrice = 1799,
            durationMins = 90,
            rating = 4.79,
            reviewCount = 890,
            whatIsIncluded = listOf(
                "Soap bubble and electronic leak detection",
                "Copper pipe leak brazing",
                "System vacuuming with two-stage rotary pump",
                "Pure OEM refrigerant gas charge (R32 / R410A / R22)"
            ),
            whatIsNotIncluded = listOf(
                "Compressor internal motor replacement",
                "Major condenser replacement"
            ),
            faqs = listOf(
                "Is there a warranty on gas charging?" to "Yes, we provide an unconditional 60-day cooling warranty."
            ),
            variants = listOf(
                ServiceVariant("gas_split_full", "Split AC Full Gas Charge", "Full system vacuum + 100% gas refill with warranty", 2199, 90),
                ServiceVariant("gas_split_topup", "Split AC Top-up & Leak Fix", "Minor leak brazing and gas top-up", 1799, 60)
            ),
            addOns = listOf(
                ServiceAddOn("addon_capacitor", "Heavy Duty Run Capacitor", 399, "Up to 50 MFD capacitor replacement")
            )
        ),
        ServiceItem(
            id = "elec_fan_install",
            categoryId = "electrical",
            title = "Fan & Decorative Light Installation",
            shortDesc = "Ceiling fans, chandeliers, spotlights and wall sconces",
            startingPrice = 199,
            durationMins = 30,
            rating = 4.86,
            reviewCount = 1980,
            whatIsIncluded = listOf(
                "Ceiling rod and safety hook installation",
                "Blade balance and regulator testing",
                "Clean wiring with insulated wire nuts"
            ),
            whatIsNotIncluded = listOf(
                "Concealed wall trenching or new conduit piping"
            ),
            faqs = listOf(
                "Do you bring the step ladder?" to "Yes, technicians carry portable folding step ladders."
            ),
            variants = listOf(
                ServiceVariant("fan_1", "1x Ceiling Fan Installation", "Mounting, balancing and connection", 199, 30),
                ServiceVariant("fan_3", "3x Ceiling Fans Combo", "Install up to 3 fans in one visit", 499, 70),
                ServiceVariant("chandelier_1", "Chandelier / Pendant Light", "Heavy decorative chandelier mounting", 449, 60)
            ),
            addOns = listOf(
                ServiceAddOn("addon_regulator", "Rotary Electronic Regulator", 149, "Hum-free step regulator installation")
            )
        ),
        ServiceItem(
            id = "plumb_tap_repair",
            categoryId = "plumbing",
            title = "Tap, Mixer & Diverter Repair",
            shortDesc = "Fix dripping faucets, internal spindle replacement, diverter issues",
            startingPrice = 199,
            durationMins = 30,
            rating = 4.84,
            reviewCount = 2150,
            whatIsIncluded = listOf(
                "Disassembly of faucet, spout or shower diverter",
                "Replacement of worn ceramic cartridge/washer (labor)",
                "Teflon tape re-sealing and pressure leak testing"
            ),
            whatIsNotIncluded = listOf(
                "Cost of new ceramic cartridges or complete brass diverter body"
            ),
            faqs = listOf(
                "Can the technician buy parts if required?" to "Yes, the technician will procure genuine spares with original GST bill."
            ),
            variants = listOf(
                ServiceVariant("tap_repair_1", "1x Tap / Faucet Repair", "Fix dripping or low pressure tap", 199, 30),
                ServiceVariant("mixer_repair", "Wall Mixer / Diverter Repair", "Fix hot-cold balance and shower leak", 399, 45),
                ServiceVariant("tap_install_new", "Install 2x New Taps", "Complete new fixture mounting", 299, 40)
            ),
            addOns = listOf(
                ServiceAddOn("addon_aerator", "Water-Saving Brass Aerator", 129, "Provides smooth foamy aerated stream")
            )
        ),
        ServiceItem(
            id = "plumb_water_tank",
            categoryId = "plumbing",
            title = "Water Tank Mechanized Disinfection",
            shortDesc = "Sludge removal, high-pressure rotary scrub & UV antibacterial treatment",
            startingPrice = 899,
            durationMins = 90,
            rating = 4.93,
            reviewCount = 670,
            whatIsIncluded = listOf(
                "Draining silt with submersible slurry pump",
                "Rotary pressure jet scrub of walls and ceiling",
                "Vacuum extraction of loose sediments",
                "Potassium permanganate antibacterial sanitization"
            ),
            whatIsNotIncluded = listOf(
                "Tank structural crack repair or concrete plastering"
            ),
            faqs = listOf(
                "When can we use the water again?" to "Immediately after refilling the tank; chemicals used are 100% food-grade safe."
            ),
            variants = listOf(
                ServiceVariant("tank_1000l", "Overhead Tank (Up to 1,000L)", "Standard Sintex/Plasto overhead tank", 899, 60),
                ServiceVariant("tank_2000l", "Overhead Tank (Up to 2,000L)", "Large capacity residential tank", 1299, 90),
                ServiceVariant("sump_3000l", "Underground Sump (Up to 3,000L)", "Deep concrete underground water sump", 1699, 120)
            ),
            addOns = listOf(
                ServiceAddOn("addon_pipe_flush", "Pipeline Chemical Flush", 399, "Flushes out rust and algae from internal pipes")
            )
        ),
        ServiceItem(
            id = "appliance_washing_machine",
            categoryId = "appliances",
            title = "Washing Machine Diagnosis & Fix",
            shortDesc = "Front load, top load & semi-automatic vibration, drainage & motor fix",
            startingPrice = 299,
            durationMins = 60,
            rating = 4.81,
            reviewCount = 1530,
            whatIsIncluded = listOf(
                "Comprehensive 20-point diagnostic check",
                "Inlet filter cleaning and pump drain test",
                "Drum balance and belt tension inspection",
                "Transparent quote before replacing any parts"
            ),
            whatIsNotIncluded = listOf(
                "Spare parts cost (pump, belt, suspension springs, PCB)"
            ),
            faqs = listOf(
                "Is the inspection fee adjusted in the repair bill?" to "Yes! The ₹299 inspection fee is 100% waived if you approve the repair."
            ),
            variants = listOf(
                ServiceVariant("wm_top_load", "Top Load Diagnosis", "Full inspection & minor fix for top load", 299, 45),
                ServiceVariant("wm_front_load", "Front Load Diagnosis", "Full inspection & diagnostic for front load", 349, 50),
                ServiceVariant("wm_deep_descale", "Machine Drum Descaling Service", "Removes internal limescale, detergent sludge", 499, 60)
            ),
            addOns = listOf(
                ServiceAddOn("addon_inlet_pipe", "High Pressure Braided Inlet Pipe", 299, "Anti-burst stainless steel braided pipe")
            )
        ),
        ServiceItem(
            id = "pest_herbal",
            categoryId = "pest_control",
            title = "Kitchen & Home Cockroach Shield",
            shortDesc = "Odorless herbal gel baiting & spray across kitchen cabinets & drains",
            startingPrice = 799,
            durationMins = 60,
            rating = 4.87,
            reviewCount = 1840,
            whatIsIncluded = listOf(
                "Bayer maxforce herbal gel dots in all cabinets & hinges",
                "Drain inlet spray to eliminate breeding nests",
                "Safe for children, senior citizens and pets; no need to vacate home"
            ),
            whatIsNotIncluded = listOf(
                "Termite wood drilling or bed bug heat eradication"
            ),
            faqs = listOf(
                "Do we need to empty all kitchen cabinets?" to "No! The gel is odorless and applied in small hidden dots without emptying dishes."
            ),
            variants = listOf(
                ServiceVariant("pest_1_2bhk", "1-2 BHK Herbal Cockroach Shield", "Full coverage with 90-day warranty", 799, 45),
                ServiceVariant("pest_3_4bhk", "3-4 BHK Herbal Cockroach Shield", "Extensive home coverage with 90-day warranty", 1199, 75)
            ),
            addOns = listOf(
                ServiceAddOn("addon_ant_defense", "Black & Red Ant Treatment", 249, "Ant path barrier spray & gel")
            )
        )
    )

    val TOP_PROS = listOf(
        ProfessionalInfo(
            id = "pro_venkat",
            name = "Venkatesh Rao",
            phone = "+91 98490 12345",
            rating = 4.94,
            reviewsCount = 512,
            experienceYears = 7,
            isPoliceVerified = true,
            specialty = "Senior HVAC & AC Engineer",
            completedJobs = 620
        ),
        ProfessionalInfo(
            id = "pro_ramesh",
            name = "Ramesh Kumar Reddy",
            phone = "+91 97010 56789",
            rating = 4.91,
            reviewsCount = 428,
            experienceYears = 6,
            isPoliceVerified = true,
            specialty = "Master Cleaner & Sanitize Lead",
            completedJobs = 580
        ),
        ProfessionalInfo(
            id = "pro_suresh",
            name = "Suresh Goud",
            phone = "+91 94400 34567",
            rating = 4.89,
            reviewsCount = 380,
            experienceYears = 8,
            isPoliceVerified = true,
            specialty = "Licensed Electrical Specialist",
            completedJobs = 490
        ),
        ProfessionalInfo(
            id = "pro_anil",
            name = "Anil Krishna",
            phone = "+91 91210 98765",
            rating = 4.88,
            reviewsCount = 290,
            experienceYears = 5,
            isPoliceVerified = true,
            specialty = "Master Plumber & Pipe Expert",
            completedJobs = 340
        )
    )

    val COUPONS = listOf(
        Coupon("WELCOME100", 100, 499, "Flat ₹100 off on your first Hyderabad service"),
        Coupon("HYDDEEP", 250, 1499, "Save ₹250 on Deep Cleaning & Overhauls"),
        Coupon("HOMEHELP50", 50, 299, "Quick ₹50 discount on any service")
    )
}
