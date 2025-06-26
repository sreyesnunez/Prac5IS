import pandas as pd
import mysql.connector
from mysql.connector import Error
import re

# Función para convertir DMS a decimal
def dms_a_decimal(coord):
    match = re.match(r"(\d+)°(\d+)'([\d\.]+)\"+ (\w)", coord.strip())
    if not match:
        return None
    grados, minutos, segundos, direccion = match.groups()
    decimal = float(grados) + float(minutos)/60 + float(segundos)/3600
    if direccion in ['S', 'W']:
        decimal *= -1
    return decimal

def importar_top_localidades():
    ruta_csv = 'localidades_limpias.csv'

    datos = pd.read_csv(ruta_csv)

    # Convertir columnas de población a numérico
    datos['pob_total'] = pd.to_numeric(datos['pob_total'], errors='coerce')
    datos['pob_masculina'] = pd.to_numeric(datos['pob_masculina'], errors='coerce')
    datos['pob_femenina'] = pd.to_numeric(datos['pob_femenina'], errors='coerce')

    # Convertir lat/lon a decimal
    datos['latitud'] = datos['latitud'].apply(dms_a_decimal)
    datos['longitud'] = datos['longitud'].apply(dms_a_decimal)

    # Agrupar por estado y seleccionar 50 más pobladas
    top_localidades = datos.sort_values(by='pob_total', ascending=False).groupby('estado').head(50)

    try:
        conexion = mysql.connector.connect(
            host='localhost',
            user='root',
            password='',
            database='proyectofinal'
        )

        if conexion.is_connected():
            cursor = conexion.cursor()
            cursor.execute("DELETE FROM localidades;")
            cursor.execute("TRUNCATE TABLE localidades;")

            for _, fila in top_localidades.iterrows():
                sql = """
                    INSERT INTO localidades (nombre, estado, latitud, longitud, pob_total, pob_masculina, pob_femenina)
                    VALUES (%s, %s, %s, %s, %s, %s, %s)
                """
                val = (
                    fila['nombre'],
                    fila['estado'],
                    fila['latitud'],
                    fila['longitud'],
                    int(fila['pob_total']) if not pd.isna(fila['pob_total']) else None,
                    int(fila['pob_masculina']) if not pd.isna(fila['pob_masculina']) else None,
                    int(fila['pob_femenina']) if not pd.isna(fila['pob_femenina']) else None
                )
                cursor.execute(sql, val)

            conexion.commit()
            print(f"✅ {cursor.rowcount} localidades insertadas.")

    except Error as e:
        print("❌ Error en la conexión o inserción:", e)

    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()
            print("🔌 Conexión cerrada.")

if __name__ == '__main__':
    importar_top_localidades()
