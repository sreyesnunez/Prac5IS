import pandas as pd

# Cargar CSV limpio
df = pd.read_csv("localidades_limpias.csv")

# Agrupar por estado y seleccionar las 100 localidades con más población
df_reducido = (
    df.sort_values("pob_total", ascending=False)
      .groupby("estado")
      .head(100)  # Cambia a 50 si quieres menos
      .reset_index(drop=True)
)

# Guardar nuevo CSV
df_reducido.to_csv("localidades_muestra.csv", index=False)
print(f"✅ Archivo generado con {len(df_reducido)} localidades (top 100 por estado).")
