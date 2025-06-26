import pandas as pd
import re

# Función para convertir coordenadas DMS a decimal
def dms_a_decimal(coord):
    if pd.isna(coord) or '*' in str(coord) or coord.strip() == '':
        return None

    match = re.match(r"(\d{1,3})°(\d{1,2})'([\d\.]+)\"+ ([NEOSW])", coord)
    if not match:
        return None

    grados, minutos, segundos, direccion = match.groups()
    decimal = float(grados) + float(minutos) / 60 + float(segundos) / 3600

    if direccion in ['S', 'W']:
        decimal *= -1

    return round(decimal, 6)

# Leer archivo original
df = pd.read_csv("inegi.csv", encoding="utf-8-sig")

# Renombrar columnas relevantes
df = df.rename(columns={
    "NOM_LOC": "nombre",
    "NOM_ENT": "estado",
    "LATITUD": "latitud_dms",
    "LONGITUD": "longitud_dms",
    "POBTOT": "pob_total",
    "POBMAS": "pob_masculina",
    "POBFEM": "pob_femenina"
})

# Filtrar filas con coordenadas válidas
df = df[df["latitud_dms"].notnull() & df["longitud_dms"].notnull()]
df = df[~df["latitud_dms"].astype(str).str.contains(r"\*")]
df = df[~df["longitud_dms"].astype(str).str.contains(r"\*")]

# Convertir coordenadas a decimal
df["latitud"] = df["latitud_dms"].apply(dms_a_decimal)
df["longitud"] = df["longitud_dms"].apply(dms_a_decimal)

# Filtrar nuevamente por coordenadas válidas
df = df[df["latitud"].notnull() & df["longitud"].notnull()]

# Limpiar campos de población
df["pob_masculina"] = pd.to_numeric(df["pob_masculina"], errors='coerce')
df["pob_femenina"] = pd.to_numeric(df["pob_femenina"], errors='coerce')

# Seleccionar columnas finales
df_final = df[["nombre", "estado", "latitud", "longitud", "pob_total", "pob_masculina", "pob_femenina"]]

# Exportar CSV limpio
df_final.to_csv("localidades_limpias.csv", index=False)

print("✅ Archivo generado: localidades_limpias.csv con", len(df_final), "localidades válidas.")
