-- Segundo lote de farmacias de prueba de MedLab (solo desarrollo, no es una migración de Flyway).
-- Se ejecuta después de datos-prueba.sql: agrega 37 farmacias aprobadas de Medellín. Las de la lista
-- que ya estaban cargadas con la misma dirección se omitieron. Coordenadas aproximadas a partir de la dirección.

SET NAMES utf8mb4;
START TRANSACTION;

SET @rol_farmacia = (SELECT id FROM roles WHERE nombre = 'farmacia');
SET @estado_aprobada = (SELECT id FROM estados_farmacia WHERE nombre = 'aprobada');
SET @hash_pruebas = '$2a$10$DatosDePruebaSinLoginTodaviaMedLabHashFicticio000000';

INSERT INTO usuarios (identificacion, correo, contrasena_hash, telefono, rol_id) VALUES
    ('929420614', 'farmacia29@medlab-pruebas.com', @hash_pruebas, '6042221268', @rol_farmacia),
    ('930213140', 'farmacia30@medlab-pruebas.com', @hash_pruebas, '6041547560', @rol_farmacia),
    ('931291531', 'farmacia31@medlab-pruebas.com', @hash_pruebas, '6047033942', @rol_farmacia),
    ('932613035', 'farmacia32@medlab-pruebas.com', @hash_pruebas, '6046562528', @rol_farmacia),
    ('933485695', 'farmacia33@medlab-pruebas.com', @hash_pruebas, '6044114659', @rol_farmacia),
    ('934740798', 'farmacia34@medlab-pruebas.com', @hash_pruebas, '6044276581', @rol_farmacia),
    ('935999034', 'farmacia35@medlab-pruebas.com', @hash_pruebas, '6041313013', @rol_farmacia),
    ('936697778', 'farmacia36@medlab-pruebas.com', @hash_pruebas, '6044661399', @rol_farmacia),
    ('937540782', 'farmacia37@medlab-pruebas.com', @hash_pruebas, '6041589499', @rol_farmacia),
    ('938489439', 'farmacia38@medlab-pruebas.com', @hash_pruebas, '6049857153', @rol_farmacia),
    ('939737672', 'farmacia39@medlab-pruebas.com', @hash_pruebas, '6048725801', @rol_farmacia),
    ('940902913', 'farmacia40@medlab-pruebas.com', @hash_pruebas, '6043461528', @rol_farmacia),
    ('941526330', 'farmacia41@medlab-pruebas.com', @hash_pruebas, '6045227319', @rol_farmacia),
    ('942622800', 'farmacia42@medlab-pruebas.com', @hash_pruebas, '6045960814', @rol_farmacia),
    ('943153732', 'farmacia43@medlab-pruebas.com', @hash_pruebas, '6043083095', @rol_farmacia),
    ('944893879', 'farmacia44@medlab-pruebas.com', @hash_pruebas, '6046324145', @rol_farmacia),
    ('945143557', 'farmacia45@medlab-pruebas.com', @hash_pruebas, '6044356522', @rol_farmacia),
    ('946658606', 'farmacia46@medlab-pruebas.com', @hash_pruebas, '6045201869', @rol_farmacia),
    ('947393313', 'farmacia47@medlab-pruebas.com', @hash_pruebas, '6048695446', @rol_farmacia),
    ('948684739', 'farmacia48@medlab-pruebas.com', @hash_pruebas, '6048864897', @rol_farmacia),
    ('949215413', 'farmacia49@medlab-pruebas.com', @hash_pruebas, '6045889282', @rol_farmacia),
    ('950754616', 'farmacia50@medlab-pruebas.com', @hash_pruebas, '6049440208', @rol_farmacia),
    ('951788532', 'farmacia51@medlab-pruebas.com', @hash_pruebas, '6046338509', @rol_farmacia),
    ('952998612', 'farmacia52@medlab-pruebas.com', @hash_pruebas, '6045907346', @rol_farmacia),
    ('953359412', 'farmacia53@medlab-pruebas.com', @hash_pruebas, '6047533153', @rol_farmacia),
    ('954111395', 'farmacia54@medlab-pruebas.com', @hash_pruebas, '6044057656', @rol_farmacia),
    ('955978664', 'farmacia55@medlab-pruebas.com', @hash_pruebas, '6042854442', @rol_farmacia),
    ('956823573', 'farmacia56@medlab-pruebas.com', @hash_pruebas, '6048882448', @rol_farmacia),
    ('957343328', 'farmacia57@medlab-pruebas.com', @hash_pruebas, '6046948330', @rol_farmacia),
    ('958827256', 'farmacia58@medlab-pruebas.com', @hash_pruebas, '6047272582', @rol_farmacia),
    ('959884512', 'farmacia59@medlab-pruebas.com', @hash_pruebas, '6042161891', @rol_farmacia),
    ('960216419', 'farmacia60@medlab-pruebas.com', @hash_pruebas, '6048080209', @rol_farmacia),
    ('961481721', 'farmacia61@medlab-pruebas.com', @hash_pruebas, '6042034646', @rol_farmacia),
    ('962703268', 'farmacia62@medlab-pruebas.com', @hash_pruebas, '6048251037', @rol_farmacia),
    ('963999165', 'farmacia63@medlab-pruebas.com', @hash_pruebas, '6041996751', @rol_farmacia),
    ('964874933', 'farmacia64@medlab-pruebas.com', @hash_pruebas, '6045262307', @rol_farmacia),
    ('965254032', 'farmacia65@medlab-pruebas.com', @hash_pruebas, '6049438973', @rol_farmacia);

INSERT INTO farmacias (usuario_id, nombre_farmacia, tipo_via_principal, numero_via_principal, letra_via_principal,
    numero_via_generadora, letra_via_generadora, numero_predio, complemento, horario_atencion,
    nit, rut, matricula_mercantil, identificacion_director_tecnico, licencia_sanitaria,
    latitud, longitud, estado_aprobacion_id) VALUES
    ((SELECT id FROM usuarios WHERE identificacion = '929420614'), 'Droguería Santo Domingo Savio', 'Carrera', '31', NULL, '107', 'A', '71', NULL, 'Lunes a sábado 7:00 a.m. - 9:00 p.m.; domingos y festivos 8:00 a.m. - 6:00 p.m.', '929420614-3', '929420614', 'MM5145796', '1031867662', 'LS-ANT-2021-3597', 6.30158355, -75.55095293, @estado_aprobada),  -- Comuna 1
    ((SELECT id FROM usuarios WHERE identificacion = '930213140'), 'Droguería Granizal', 'Calle', '105', NULL, '38', 'A', '36', NULL, 'Lunes a domingo 7:00 a.m. - 10:00 p.m.', '930213140-3', '930213140', 'MM2326887', '501487538', 'LS-ANT-2024-8783', 6.30017941, -75.55766140, @estado_aprobada),  -- Comuna 1
    ((SELECT id FROM usuarios WHERE identificacion = '931291531'), 'Dropopular S.A.S.', 'Carrera', '74', NULL, '53', NULL, '145', NULL, 'Abierto 24 horas', '931291531-1', '931291531', 'MM1703837', '919372777', 'LS-ANT-2021-7798', 6.24962831, -75.59165760, @estado_aprobada),  -- Comuna 1
    ((SELECT id FROM usuarios WHERE identificacion = '932613035'), 'Droguería Santa Cruz (Sede Socorro)', 'Calle', '104', NULL, '13', NULL, '1', 'Sede Socorro', 'Lunes a sábado 8:00 a.m. - 8:00 p.m.', '932613035-5', '932613035', 'MM7501463', '811946896', 'LS-ANT-2025-7466', 6.29750000, -75.56000000, @estado_aprobada),  -- Comuna 2
    ((SELECT id FROM usuarios WHERE identificacion = '933485695'), 'Farmacia Santa Cruz', 'Calle', '57', NULL, '50', 'A', '58', NULL, 'Lunes a sábado 7:00 a.m. - 9:00 p.m.; domingos y festivos 8:00 a.m. - 6:00 p.m.', '933485695-6', '933485695', 'MM2261933', '627282403', 'LS-ANT-2023-2896', 6.25298359, -75.56888677, @estado_aprobada),  -- Comuna 2
    ((SELECT id FROM usuarios WHERE identificacion = '934740798'), 'Depósito Farmacéutico Tododrogas', 'Carrera', '48', NULL, '49', NULL, '57', NULL, 'Abierto 24 horas', '934740798-1', '934740798', 'MM4361405', '190818872', 'LS-ANT-2021-9405', 6.24532102, -75.56699491, @estado_aprobada),  -- Comuna 2
    ((SELECT id FROM usuarios WHERE identificacion = '935999034'), 'Vía Baloto Droguería Manrique', 'Carrera', '45', NULL, '68', NULL, '46', NULL, 'Abierto 24 horas', '935999034-5', '935999034', 'MM7742245', '489112309', 'LS-ANT-2020-6393', 6.26375675, -75.56462844, @estado_aprobada),  -- Comuna 3
    ((SELECT id FROM usuarios WHERE identificacion = '936697778'), 'Droguería Aranjuez', 'Carrera', '49', NULL, '116', NULL, '00', NULL, 'Lunes a sábado 8:00 a.m. - 8:00 p.m.', '936697778-7', '936697778', 'MM3223949', '1088150755', 'LS-ANT-2026-1890', 6.31091580, -75.56830988, @estado_aprobada),  -- Comuna 4
    ((SELECT id FROM usuarios WHERE identificacion = '937540782'), 'Droguería Castilla', 'Carrera', '68', NULL, '95', NULL, '64', NULL, 'Lunes a sábado 7:00 a.m. - 9:00 p.m.; domingos y festivos 8:00 a.m. - 6:00 p.m.', '937540782-1', '937540782', 'MM1032647', '151355050', 'LS-ANT-2022-4245', 6.29030571, -75.58627087, @estado_aprobada),  -- Comuna 5
    ((SELECT id FROM usuarios WHERE identificacion = '938489439'), 'Droguería Santa Mónica', 'Carrera', '68', NULL, '96', NULL, '121', NULL, 'Lunes a domingo 7:00 a.m. - 10:00 p.m.', '938489439-4', '938489439', 'MM4357357', '650094988', 'LS-ANT-2021-6601', 6.29091119, -75.58574395, @estado_aprobada),  -- Comuna 5
    ((SELECT id FROM usuarios WHERE identificacion = '939737672'), 'Droguería Versalles', 'Calle', '98', NULL, '68', NULL, '20', NULL, 'Lunes a sábado 7:00 a.m. - 9:00 p.m.; domingos y festivos 8:00 a.m. - 6:00 p.m.', '939737672-0', '939737672', 'MM1259780', '631374088', 'LS-ANT-2024-9081', 6.29292165, -75.58617949, @estado_aprobada),  -- Comuna 5
    ((SELECT id FROM usuarios WHERE identificacion = '940902913'), 'Droguería Dromarín', 'Carrera', '79', NULL, '92', NULL, '93', NULL, 'Abierto 24 horas', '940902913-8', '940902913', 'MM5982071', '184344662', 'LS-ANT-2026-5282', 6.28728223, -75.59644048, @estado_aprobada),  -- Comuna 6
    ((SELECT id FROM usuarios WHERE identificacion = '941526330'), 'Farmatodo', 'Calle', '53', NULL, '73', NULL, '20', NULL, 'Abierto 24 horas', '941526330-6', '941526330', 'MM3741464', '165619745', 'LS-ANT-2026-6306', 6.24923491, -75.59084933, @estado_aprobada),  -- Comuna 6
    ((SELECT id FROM usuarios WHERE identificacion = '942622800'), 'Droguería La Campiña', 'Carrera', '94', 'B', '64', 'A', '04', NULL, 'Lunes a sábado 8:00 a.m. - 8:00 p.m.', '942622800-5', '942622800', 'MM4888762', '749434430', 'LS-ANT-2025-7558', 6.26008367, -75.61069494, @estado_aprobada),  -- Comuna 7
    ((SELECT id FROM usuarios WHERE identificacion = '943153732'), 'La Cápsula Droguería', 'Calle', '70', NULL, '52', NULL, '66', NULL, 'Abierto 24 horas', '943153732-5', '943153732', 'MM5019275', '233449513', 'LS-ANT-2022-6832', 6.26626612, -75.57110381, @estado_aprobada),  -- Comuna 7
    ((SELECT id FROM usuarios WHERE identificacion = '944893879'), 'Droguería Surtidrogas La Villa', 'Carrera', '40', NULL, '65', 'A', '39', NULL, 'Abierto 24 horas', '944893879-4', '944893879', 'MM2349954', '337752836', 'LS-ANT-2026-3342', 6.26065922, -75.55959017, @estado_aprobada),  -- Comuna 8
    ((SELECT id FROM usuarios WHERE identificacion = '945143557'), 'Droguería La Villa del Parque', 'Calle', '66', NULL, '39', NULL, '25', NULL, 'Lunes a sábado 7:00 a.m. - 9:00 p.m.; domingos y festivos 8:00 a.m. - 6:00 p.m.', '945143557-6', '945143557', 'MM1160476', '563688630', 'LS-ANT-2022-4996', 6.26206209, -75.55851891, @estado_aprobada),  -- Comuna 8
    ((SELECT id FROM usuarios WHERE identificacion = '946658606'), 'Farmacia Surtifarma Buenos Aires', 'Calle', '42', NULL, '29', NULL, '14', NULL, 'Lunes a domingo 7:00 a.m. - 10:00 p.m.', '946658606-4', '946658606', 'MM9143054', '893170912', 'LS-ANT-2026-4837', 6.23851622, -75.54925601, @estado_aprobada),  -- Comuna 9
    ((SELECT id FROM usuarios WHERE identificacion = '947393313'), 'Farmacia Nueva Vital', 'Carrera', '27', NULL, '45', NULL, '1', 'Barrio Gerona', 'Lunes a domingo 7:00 a.m. - 10:00 p.m.', '947393313-3', '947393313', 'MM7511400', '913874367', 'LS-ANT-2024-5745', 6.24050000, -75.54900000, @estado_aprobada),  -- Comuna 9
    ((SELECT id FROM usuarios WHERE identificacion = '948684739'), 'Droguería La Bastilla Berrío', 'Avenida', '50', NULL, '51', NULL, '20', 'Avenida Palacé', 'Abierto 24 horas', '948684739-3', '948684739', 'MM6707385', '642070499', 'LS-ANT-2023-5359', 6.24718007, -75.56872525, @estado_aprobada),  -- Comuna 10
    ((SELECT id FROM usuarios WHERE identificacion = '949215413'), 'Droguería El Palmar', 'Calle', '43', NULL, '40', NULL, '09', NULL, 'Abierto 24 horas', '949215413-6', '949215413', 'MM1145040', '55364947', 'LS-ANT-2025-3618', 6.23998518, -75.55964705, @estado_aprobada),  -- Comuna 10
    ((SELECT id FROM usuarios WHERE identificacion = '950754616'), 'Droguería Laureles', 'Carrera', '73', NULL, '41', NULL, '49', NULL, 'Lunes a sábado 7:00 a.m. - 9:00 p.m.; domingos y festivos 8:00 a.m. - 6:00 p.m.', '950754616-2', '950754616', 'MM8528802', '854740384', 'LS-ANT-2026-5837', 6.23749434, -75.59084417, @estado_aprobada),  -- Comuna 11
    ((SELECT id FROM usuarios WHERE identificacion = '951788532'), 'Audifarma Laureles', 'Calle', '44', NULL, '79', 'B', '145', NULL, 'Lunes a domingo 7:00 a.m. - 10:00 p.m.', '951788532-5', '951788532', 'MM7199500', '799360282', 'LS-ANT-2020-6710', 6.24093292, -75.59598823, @estado_aprobada),  -- Comuna 11
    ((SELECT id FROM usuarios WHERE identificacion = '952998612'), 'Locatel Viva Laureles', 'Carrera', '81', NULL, '37', NULL, '100', 'Local 017', 'Lunes a sábado 8:00 a.m. - 8:00 p.m.', '952998612-0', '952998612', 'MM1953537', '1084090452', 'LS-ANT-2020-1368', 6.23362683, -75.59853095, @estado_aprobada),  -- Comuna 11
    ((SELECT id FROM usuarios WHERE identificacion = '953359412'), 'Droguería Super Descuentos', 'Carrera', '84', NULL, '45', NULL, '40', NULL, 'Lunes a sábado 7:00 a.m. - 9:00 p.m.; domingos y festivos 8:00 a.m. - 6:00 p.m.', '953359412-9', '953359412', 'MM5426207', '471371595', 'LS-ANT-2022-6644', 6.24195298, -75.60081454, @estado_aprobada),  -- Comuna 12
    ((SELECT id FROM usuarios WHERE identificacion = '954111395'), 'Droguería Majarí', 'Carrera', '88', NULL, '39', NULL, '02', NULL, 'Lunes a sábado 7:00 a.m. - 9:00 p.m.; domingos y festivos 8:00 a.m. - 6:00 p.m.', '954111395-7', '954111395', 'MM6744034', '495884458', 'LS-ANT-2021-6271', 6.23547975, -75.60457777, @estado_aprobada),  -- Comuna 12
    ((SELECT id FROM usuarios WHERE identificacion = '955978664'), 'Droguería Metro Avenida', 'Carrera', '99', NULL, '47', 'A', '75', NULL, 'Abierto 24 horas', '955978664-4', '955978664', 'MM3078062', '779703580', 'LS-ANT-2022-2895', 6.24398470, -75.61503562, @estado_aprobada),  -- Comuna 13
    ((SELECT id FROM usuarios WHERE identificacion = '956823573'), 'Droguería Pharma Salud', 'Calle', '43', NULL, '99', NULL, '10', NULL, 'Lunes a sábado 7:00 a.m. - 9:00 p.m.; domingos y festivos 8:00 a.m. - 6:00 p.m.', '956823573-7', '956823573', 'MM1052700', '614478031', 'LS-ANT-2021-7588', 6.23979338, -75.61466814, @estado_aprobada),  -- Comuna 13
    ((SELECT id FROM usuarios WHERE identificacion = '957343328'), 'Super Drogas San Javier', 'Calle', '43', NULL, '101', NULL, '49', NULL, 'Abierto 24 horas', '957343328-9', '957343328', 'MM6768649', '454171675', 'LS-ANT-2021-2610', 6.23953386, -75.61679558, @estado_aprobada),  -- Comuna 13
    ((SELECT id FROM usuarios WHERE identificacion = '958827256'), 'Farmacia Pasteur Avenida El Poblado', 'Carrera', '43', NULL, '25', 'A', '4', NULL, 'Lunes a domingo 7:00 a.m. - 10:00 p.m.', '958827256-8', '958827256', 'MM1861605', '669122922', 'LS-ANT-2026-6472', 6.22241472, -75.56228956, @estado_aprobada),  -- Comuna 14
    ((SELECT id FROM usuarios WHERE identificacion = '959884512'), 'Drogas Calidad Sede Poblado', 'Carrera', '35', NULL, '15', 'B', '35', 'Local 108', 'Lunes a sábado 8:00 a.m. - 8:00 p.m.', '959884512-6', '959884512', 'MM5385440', '284850333', 'LS-ANT-2023-3691', 6.21270853, -75.55451844, @estado_aprobada),  -- Comuna 14
    ((SELECT id FROM usuarios WHERE identificacion = '960216419'), 'Farmacia Colsubsidio Guayabal', 'Calle', '3', NULL, '51', 'B', '18', 'Calle 3 Sur', 'Lunes a sábado 8:00 a.m. - 8:00 p.m.', '960216419-0', '960216419', 'MM1463644', '1068620397', 'LS-ANT-2022-2373', 6.19510000, -75.56990000, @estado_aprobada),  -- Comuna 15
    ((SELECT id FROM usuarios WHERE identificacion = '961481721'), 'Farmaster Droguerías Guayabal', 'Calle', '16', NULL, '58', 'A', '24', NULL, 'Abierto 24 horas', '961481721-0', '961481721', 'MM4697952', '23768504', 'LS-ANT-2020-4193', 6.21365329, -75.57628617, @estado_aprobada),  -- Comuna 15
    ((SELECT id FROM usuarios WHERE identificacion = '962703268'), 'Drogas La Garantía Rodeo', 'Calle', '41', NULL, '40', 'A', '26', NULL, 'Lunes a sábado 7:00 a.m. - 9:00 p.m.; domingos y festivos 8:00 a.m. - 6:00 p.m.', '962703268-2', '962703268', 'MM8432876', '1049266547', 'LS-ANT-2026-5333', 6.23789842, -75.55987944, @estado_aprobada),  -- Comuna 15
    ((SELECT id FROM usuarios WHERE identificacion = '963999165'), 'La Rebaja Plus No. 22 (Parque Belén)', 'Calle', '30', 'A', '76', NULL, '45', NULL, 'Abierto 24 horas', '963999165-0', '963999165', 'MM9887906', '52636325', 'LS-ANT-2025-5731', 6.22673983, -75.59336830, @estado_aprobada),  -- Comuna 16
    ((SELECT id FROM usuarios WHERE identificacion = '964874933'), 'Droguería Belén', 'Carrera', '76', NULL, '25', NULL, '04', NULL, 'Lunes a sábado 8:00 a.m. - 8:00 p.m.', '964874933-6', '964874933', 'MM1057339', '175136901', 'LS-ANT-2026-4399', 6.22218379, -75.59331774, @estado_aprobada),  -- Comuna 16
    ((SELECT id FROM usuarios WHERE identificacion = '965254032'), 'Droguería Farmabelén', 'Calle', '31', NULL, '77', NULL, '26', NULL, 'Lunes a sábado 8:00 a.m. - 8:00 p.m.', '965254032-0', '965254032', 'MM9856241', '102792868', 'LS-ANT-2022-4632', 6.22784128, -75.59441184, @estado_aprobada);  -- Comuna 16

COMMIT;
