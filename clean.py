import pandas as pd

# Cargar el archivo CSV
file_path = 'ITER_NALCSV20.csv'  # Asegúrate de usar el path correcto
data = pd.read_csv(file_path, encoding='utf-8-sig')  # Usa utf-8-sig para manejar BOM

# Renombrar columnas para remover caracteres invisibles/BOM si es necesario
data.columns = data.columns.str.replace(r'^\ï»¿', '', regex=True)

# Imprime los nombres de las columnas para verificar que sean correctos
print("Columnas en el archivo después de limpiar:", data.columns.tolist())

# Procesamiento de los datos
columns_to_keep = [
    'ENTIDAD', 'NOM_ENT', 'MUN', 'NOM_MUN', 'LOC', 'NOM_LOC',
    'LONGITUD', 'LATITUD', 'POBTOT', 'POBMAS', 'POBFEM'
]

# Asegúrate que todas las columnas necesarias están
data_cleaned = data[columns_to_keep]

# Más procesamiento como filtrar, limpiar datos, etc.
# ...

# Guardar a un nuevo archivo si es necesario
data_cleaned.to_csv('inegi.csv', index=False)
