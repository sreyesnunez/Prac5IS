import pandas as pd
import mysql.connector
from mysql.connector import Error

def importar_datos():
    # Ruta al archivo CSV
    ruta_csv = 'sismos.csv'
    
    # Leer datos desde CSV
    datos = pd.read_csv(ruta_csv)

    # Verificar nombres de columnas
    print("Nombres de columnas:", datos.columns)

    try:
        # Conexión a la base de datos
        conexion = mysql.connector.connect(
            host='localhost',
            user='root',
            password='',
            database='proyectofinal'
        )

        if conexion.is_connected():
            cursor = conexion.cursor()
            # Insertar datos
            for _, fila in datos.iterrows():
                sql = """INSERT INTO Sismos (Fecha, Hora, Magnitud, Latitud, Longitud, Profundidad, 
                         Referencia_localizacion, Fecha_UTC, Hora_UTC, Estatus)
                         VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"""
                val = (fila['Fecha'], fila['Hora'], fila['Magnitud'], fila['Latitud'], fila['Longitud'],
                       fila['Profundidad'], fila['Referencia de localizacion'], fila['Fecha UTC'],
                       fila['Hora UTC'], fila['Estatus'])
                cursor.execute(sql, val)

            conexion.commit()
            print(f"{cursor.rowcount} registros insertados.")
            
    except Error as e:
        print("Error durante la conexión a MySQL:", e)
    finally:
        if conexion.is_connected():
            cursor.close()
            conexion.close()
            print("Conexión a MySQL cerrada")

if __name__ == '__main__':
    importar_datos()
