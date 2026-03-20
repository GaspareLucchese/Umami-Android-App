from flask import (
    Blueprint, flash, g, redirect, render_template, 
    request, session, url_for, jsonify, current_app, Flask
)
from flask.logging import default_handler
from datetime import datetime, timedelta, date
from mysql.connector import Error
from . import db

import random
import datetime
import logging

app = Flask(__name__)
logging.basicConfig(level = logging.INFO)


bp = Blueprint('pwm', __name__, url_prefix='/pwm')

@bp.route('/utenti', methods=['GET'])
def user(): 
    if request.method == 'GET':
        connection = db.getdb()
        resp = []
        try:
            cursor = connection.cursor(dictionary=True)
            cursor.execute("SELECT * FROM utente")
            query_result = cursor.fetchall()
            resp = parsToJson(query_result, cursor.column_names)
        except db.Error as e:
            app.logger.error(f"Database error: {str(e)}")
        finally:        
            cursor.close()
    return jsonify(resp)

@bp.route('/utenti/email', methods=['GET'])
def get_user_by_email():
    email = request.args.get('email')
    connection = db.getdb()
    resp = {}
    try:
        cursor = connection.cursor(dictionary=True)
        cursor.execute("SELECT * FROM utente WHERE email = %s", (email,))
        query_result = cursor.fetchone()
        if query_result:
            resp = query_result
        else:
            resp = {'error': 'User not found'}
    except db.Error as e:
        app.logger.error(f"Database error: {str(e)}")
    finally:
        cursor.close()
        connection.close()
    return jsonify(resp)


@bp.route('/utenti/indirizzo', methods=['GET'])
def get_address_by_email():
    email = request.args.get('email')
    connection = db.getdb()
    indirizzo = ""
    try:
        cursor = connection.cursor()
        cursor.execute("SELECT indirizzo FROM utente WHERE email = %s", (email,))
        query_result = cursor.fetchone()
        if query_result:
            indirizzo = query_result[0]  # assuming indirizzo is the first column in the result
        else:
            indirizzo = 'User not found'
    except db.Error as e:
        app.logger.error(f"Database error: {str(e)}")
        indirizzo = 'Database error'
    finally:
        cursor.close()
        connection.close()
    return indirizzo

@bp.route('/utenti', methods=['POST'])
def create_user():
    nome = request.json.get('nome')
    cognome = request.json.get('cognome')
    email = request.json.get('email')
    indirizzo = request.json.get('indirizzo')
    password = request.json.get('password')
    carta = request.json.get('carta')
    intestatario = request.json.get('intestatario')
    meseScadenza = request.json.get('meseScadenza')
    annoScadenza = request.json.get('annoScadenza')
    codiceCvv = request.json.get('codiceCvv')
    connection = db.getdb()
    try:
        cursor = connection.cursor()
        cursor.execute("INSERT INTO utente (nome, cognome, indirizzo, password, email, carta, intestatario, meseScadenza, annoScadenza, codiceCvv) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", (nome, cognome, indirizzo, password, email, carta, intestatario, meseScadenza, annoScadenza, codiceCvv))
        connection.commit()
        user_id = cursor._last_insert_id
        resp = {
            'idutente': user_id,
            'nome': nome,
            'cognome': cognome,
            'indirizzo': indirizzo,
            'password': password,
            'email': email,
            'carta': carta,
            'intestatario': intestatario,
            'meseScadenza': meseScadenza,
            'annoScadenza': annoScadenza,
            'codiceCvv': codiceCvv
        }
    except db.Error as e:
        app.logger.error(f"Database error: {str(e)}")
    finally:
        cursor.close()
    return jsonify(resp)


@bp.route('/assistenze', methods=['POST'])
def create_assistenza():
    email = request.json.get('email')
    messaggio = request.json.get('messaggio')

    if not email or not messaggio:
        return jsonify({'errore': 'Email e messaggio sono richiesti'})

    connection = db.getdb()
    try:
        cursor = connection.cursor()
        cursor.execute("INSERT INTO assistenza (RefUtenteAssistenza, messaggio) VALUES (%s, %s)", (email, messaggio))
        connection.commit()
        assistenza_id = cursor.lastrowid
        resp = {
            'id': assistenza_id,
            'email': email,
            'messaggio': messaggio
        }
        return jsonify(resp)
    except Error as e:
        return jsonify({'errore': str(e)})
    finally:
        cursor.close()
        connection.close()

    return jsonify(resp)

@bp.route('/utenti/email/<string:email>', methods=['PUT'])
def update_user_by_email(email):
    data = request.json
    nome = data.get('nome')
    cognome = data.get('cognome')
    new_email = data.get('email')
    indirizzo = data.get('indirizzo')
    password = data.get('password')

    connection = db.getdb()
    try:
        cursor = connection.cursor()
        cursor.execute(
            "UPDATE utente SET nome = %s, cognome = %s, email = %s, indirizzo = %s, password = %s WHERE email = %s",
            (nome, cognome, new_email, indirizzo, password, email)
        )
        if cursor.rowcount == 0:
            resp = {'error': 'User not found'}
        else:
            connection.commit()
            resp = {
                'nome': nome,
                'cognome': cognome,
                'email': new_email,
                'indirizzo': indirizzo,
                'password': password
            }
    except db.Error as e:
        app.logger.error(f"Database error: {str(e)}")
    finally:
        cursor.close()
        connection.close()
    return jsonify(resp)

@bp.route('/utenti/transazioni/email/<string:email>', methods=['PUT'])
def update_transazioni(email):
    data = request.json
    intestatario = data.get('intestatario')
    carta = data.get('carta')
    mese_scadenza = data.get('meseScadenza')
    anno_scadenza = data.get('annoScadenza')
    codice_cvv = data.get('codiceCvv')

    connection = db.getdb()
    try:
        cursor = connection.cursor()
        cursor.execute(
            """UPDATE utente SET intestatario = %s, carta = %s, meseScadenza = %s, annoScadenza = %s, codiceCvv = %s WHERE email = %s""",
            (intestatario, carta, mese_scadenza, anno_scadenza, codice_cvv, email)
        )
        if cursor.rowcount == 0:
            resp = {'error': 'User not found'}
        else:
            connection.commit()
            resp = {
                'intestatario': intestatario,
                'carta': carta,
                'meseScadenza': mese_scadenza,
                'annoScadenza': anno_scadenza,
                'codiceCvv': codice_cvv
            }
    except db.Error as e:
        app.logger.error(f"Database error: {str(e)}")
    finally:
        cursor.close()
        connection.close()
    return jsonify(resp)

@bp.route('/utenti/<int:user_id>', methods=['DELETE'])
def delete_user(user_id):
    connection = db.getdb()
    try:
        cursor = connection.cursor()
        cursor.execute("DELETE FROM utente WHERE idutente = %s", (user_id,))
        connection.commit()
        resp = {'message': 'User deleted successfully'}
    except db.Error as e:
        app.logger.error(f"Database error: {str(e)}")
    finally:
        cursor.close()
    return jsonify(resp)


'''
@bp.route('/prodotti', methods=['GET'])
def item(): 
    if request.method == 'GET':
        connection = db.getdb()
        resp = []
        try:
            cursor = connection.cursor(dictionary=True)
            cursor.execute("SELECT * FROM prodotto WHERE nPezzi > 0")
            query_result = cursor.fetchall()
            resp = parsToJson(query_result, cursor.column_names)
        except db.Error as e:
            app.logger.error(f"Database error: {str(e)}")
        finally:        
            cursor.close()
    return jsonify(resp)
'''

@bp.route('/prodotti/nuovi', methods=['GET'])
def newItem(): 
    if request.method == 'GET':
        connection = db.getdb()
        resp = []
        try:
            cursor = connection.cursor(dictionary=True)

            #calcoliamo prima la data di 30 giorni fa
            oggi = date.today()
            dataMesePrima = oggi - timedelta(days=30)


            query = "SELECT * FROM prodotto WHERE dataInserimento >= %s and nPezzi > 0 ORDER BY dataInserimento DESC"
            cursor.execute(query, (dataMesePrima,))
            query_result = cursor.fetchall()
            resp = parsToJson(query_result, cursor.column_names)
        except db.Error as e:
            app.logger.error(f"Database error: {str(e)}")
        finally:        
            cursor.close()
    return jsonify(resp)

@bp.route('/prodotti/bestsellers', methods=['GET'])
def bestsellersItem(): 
    if request.method == 'GET':
        connection = db.getdb()
        resp = []
        try:
            cursor = connection.cursor(dictionary=True)
            #mostriamo solo i primi 20 prodotti più venduti
            query = "SELECT p.idProdotto, p.name, p.price, p.categoria, p.resource, p.starImage, p.description, p.dataInserimento, p.nPezzi, p.dataRestock, p.nValutazioni, COUNT(o.idordine) AS numero_ordini FROM ordine o, prodotto p WHERE o.RefProdotto = p.idProdotto and p.nPezzi > 0 GROUP BY p.idProdotto ORDER BY numero_ordini DESC LIMIT 20"
            cursor.execute(query, )
            query_result = cursor.fetchall()
            resp = parsToJson(query_result, cursor.column_names)
        except db.Error as e:
            app.logger.error(f"Database error: {str(e)}")
        finally:        
            cursor.close()
    return jsonify(resp)

@bp.route('/prodotti/offerte', methods=['GET'])
def offersItem(): 
    if request.method == 'GET':
        connection = db.getdb()
        resp = []
        try:
            cursor = connection.cursor(dictionary=True)
            query = "SELECT * FROM prodotto WHERE flagOfferta = 1 and nPezzi > 0 ORDER BY rand()"
            cursor.execute(query, )
            query_result = cursor.fetchall()
            resp = parsToJson(query_result, cursor.column_names)
        except db.Error as e:
            app.logger.error(f"Database error: {str(e)}")
        finally:        
            cursor.close()
    return jsonify(resp)


@bp.route('/prodotti/restock', methods=['GET'])
def restockItem(): 
    if request.method == 'GET':
        connection = db.getdb()
        resp = []
        try:
            cursor = connection.cursor(dictionary=True)

            #calcoliamo prima la data di 30 giorni fa
            oggi = date.today()
            dataMesePrima = oggi - timedelta(days=30)

            query = "SELECT * FROM prodotto WHERE dataRestock >= %s and nPezzi > 0 ORDER BY dataRestock DESC"
            cursor.execute(query, (dataMesePrima,))
            query_result = cursor.fetchall()
            resp = parsToJson(query_result, cursor.column_names)
        except db.Error as e:
            app.logger.error(f"Database error: {str(e)}")
        finally:        
            cursor.close()
    return jsonify(resp)


@bp.route('/prodotti/categoria', methods=['GET'])
def categoryItem():
    categoria = request.args.get('categoria')
    connection = db.getdb()
    result = []
    try:
        cursor = connection.cursor(dictionary=True)
        if categoria:
            cursor.execute("SELECT * FROM prodotto WHERE categoria = %s and nPezzi > 0 ORDER BY name", (categoria,))
            query_result = cursor.fetchall()
            if query_result:
                result = parsToJson(query_result, cursor.column_names)
        # Non serve l'else per 'Categoria non fornita' poiché restituiamo solo un array vuoto
    except db.Error as e:
        app.logger.error(f"Database error: {str(e)}")
    finally:
        cursor.close()
        connection.close()
    return jsonify(result)

@bp.route('/ordini/nonconsegnati', methods=['GET'])
def notDelivered():
    RefUtente = request.args.get('RefUtente')
    flagConsegnato = 0
    connection = db.getdb()
    result = []
    try:
        cursor = connection.cursor(dictionary=True)
        cursor.execute("SELECT o.idordine, o.codiceSpedizione, o.flagConsegnato, o.valutazioneOrdine, o.RefUtente, o.RefProdotto, o.dataConsegna, p.name, p.resource, o.price, o.indirizzo FROM ordine o, prodotto p WHERE o.flagConsegnato = %s and o.RefProdotto = p.idProdotto and o.RefUtente = %s order by o.idordine desc", (flagConsegnato, RefUtente, ))
        query_result = cursor.fetchall()
        if query_result:
            result = parsToJson(query_result, cursor.column_names)
    except db.Error as e:
        app.logger.error(f"Database error: {str(e)}")
    finally:
        cursor.close()
        connection.close()
    return jsonify(result)

@bp.route('/ordini/consegnati', methods=['GET'])
def delivered():
    RefUtente = request.args.get('RefUtente')
    flagConsegnato = 1
    connection = db.getdb()
    result = []
    try:
        cursor = connection.cursor(dictionary=True)
        cursor.execute("SELECT o.idordine, o.codiceSpedizione, o.flagConsegnato, o.valutazioneOrdine, o.RefUtente, o.RefProdotto, o.dataConsegna, p.name, o.price, p.resource, o.indirizzo FROM ordine o, prodotto p WHERE o.flagConsegnato = %s and o.RefProdotto = p.idProdotto and o.RefUtente = %s order by o.idordine desc", (flagConsegnato, RefUtente, ))
        query_result = cursor.fetchall()
        if query_result:
            result = parsToJson(query_result, cursor.column_names)
    except db.Error as e:
        app.logger.error(f"Database error: {str(e)}")
    finally:
        cursor.close()
        connection.close()
    return jsonify(result)

@bp.route('/ordini', methods=['POST'])
def create_order():
    prefisso = "IT-"
    suffisso = ''.join([str(random.randint(0, 9)) for i in range(10)])
    codSpedizione = prefisso + suffisso
    flagConsegnato = 0
    email = request.json.get('RefUtente')
    idProdotto = request.json.get('RefProdotto')
    indirizzo = request.json.get('indirizzo')
    price = request.json.get('price')
    connection = db.getdb()
    try:
        cursor = connection.cursor()
        cursor.execute("INSERT INTO ordine (codiceSpedizione, flagConsegnato, RefUtente, RefProdotto, indirizzo, price) VALUES (%s, %s, %s, %s, %s, %s)", 
                       (codSpedizione, flagConsegnato, email, idProdotto, indirizzo, price))
        connection.commit()
        user_id = cursor.lastrowid
        resp = {
            'id': user_id,
            'codiceSpedizione': codSpedizione,
            'flagConsegnato': flagConsegnato,
            'valutazioneOrdine': None,
            'RefUtente': email,
            'RefProdotto': idProdotto,
            'dataConsegna': None,
            'indirizzo': indirizzo,
            'price': price
        }
    except Error as e:
        logging.error(f"Database error: {str(e)}")
        resp = {'error': 'Database error'}
    finally:
        cursor.close()
        connection.close()
    return jsonify(resp)


@bp.route('/ordini/idordine/<int:idordine>', methods=['PUT'])
def update_rating_order(idordine):
    data = request.json
    valutazioneOrdine = data.get('valutazioneOrdine')
    resp = {}

    connection = db.getdb()
    try:
        cursor = connection.cursor()
        cursor.execute(
            "UPDATE ordine o SET o.valutazioneOrdine = %s WHERE o.idordine = %s", (valutazioneOrdine, idordine))
        if cursor.rowcount == 0:
            resp = {'error': 'Order not found'}
        else:
            connection.commit()
            resp = {
                'idordine': idordine,
                'valutazioneOrdine': valutazioneOrdine,
                'message': 'Order rating updated successfully'
            }
    except Error as e:
        return jsonify({'errore': str(e)})
    finally:
        cursor.close()
        connection.close()

    return jsonify(resp), 200 if 'error' not in resp else 400




@bp.route('/prodotti/idProdotto/<int:idProdotto>', methods=['PUT'])
def update_rating_item(idProdotto):
    data = request.json
    starImage = data.get('starImage')

    connection = db.getdb()
    try:
        cursor = connection.cursor()
        cursor.execute(
            "UPDATE prodotto p SET p.starImage = ((p.starImage * p.nValutazioni) + %s) / (p.nValutazioni + 1), p.nValutazioni = p.nValutazioni + 1 WHERE p.idProdotto = %s;", (starImage, idProdotto))
        if cursor.rowcount == 0:
            resp = {'error': 'Item not found'}
        else:
            connection.commit()
            resp = {
                'idProdotto': idProdotto,
                'starImage': starImage
            }
    except Error as e:
        return jsonify({'errore': str(e)})
    finally:
        cursor.close()
        connection.close()
    return jsonify(resp)

@bp.route('/acquistaDiNuovo', methods=['GET'])
def againItem():
    email = request.args.get('email')
    connection = db.getdb()
    result = []
    try:
        cursor = connection.cursor(dictionary=True)
        if email:
            cursor.execute("SELECT DISTINCT p.idProdotto, p.name, p.price, p.categoria, p.resource, p.starImage, p.description, p.dataInserimento, p.nPezzi, p.dataRestock FROM ordine o, prodotto p WHERE o.RefUtente = %s and o.RefProdotto = p.idProdotto and p.nPezzi > 0", (email,))
            query_result = cursor.fetchall()
            if query_result:
                result = parsToJson(query_result, cursor.column_names)
        # Non serve l'else per 'Categoria non fornita' poiché restituiamo solo un array vuoto
    except db.Error as e:
        app.logger.error(f"Database error: {str(e)}")
    finally:
        cursor.close()
        connection.close()
    return jsonify(result)

@bp.route('/prodotti/idProdotto/<int:idProdotto>', methods=['PUT'])
def update_numItems(idProdotto):
    data = request.json
    nPezzi = data.get('nPezzi')
    nPezziUpdated = nPezzi - 1
    
    connection = db.getdb()
    try:
        cursor = connection.cursor()
        cursor.execute(
            "UPDATE prodotto SET nPezzi = %s WHERE idProdotto = %s",
            (nPezziUpdated, idProdotto)
        )
        if cursor.rowcount == 0:
            resp = {'error': 'Item not found'}
        else:
            connection.commit()
            resp = {
                'prodotto': idProdotto,
                'nPezzi': nPezziUpdated
            }
    except db.Error as e:
        app.logger.error(f"Database error: {str(e)}")
    finally:
        cursor.close()
        connection.close()
    return jsonify(resp)


@bp.route('/prodotti/ricerca', methods=['GET'])
def get_lista_ricerca_prodotti():
    name = request.args.get('name')
    #current_app.logger.debug(f"Request received with name: {name}")

    if not name:
        return jsonify({"error": "Nome è un campo obbligatorio"}), 400

    connection = db.getdb()
    try:
        cursor = connection.cursor(dictionary=True)
        cursor.execute("""
            SELECT idProdotto, name, price, starImage, resource, description, nPezzi
            FROM prodotto 
            WHERE nPezzi > 0 and name LIKE %s
        """, ("%" + name + "%",))  # Utilizza i wildcard per la ricerca parziale
        results = cursor.fetchall()

        item_list = []
        for row in results:
            item = {
                "idProdotto": row["idProdotto"],
                "name": row["name"],
                "price": row["price"],
                "starImage": row["starImage"],
                "resource": row["resource"],
                "description" : row["description"],
                "nPezzi" : row["nPezzi"]
            }
            item_list.append(item)

        #current_app.logger.debug(f"Response: {item_list}")
        return jsonify(item_list), 200
    except Exception as e:
        #current_app.logger.error(f"Error: {str(e)}")
        return jsonify({"error": f"Errore durante la ricerca dei prodotti: {str(e)}"}), 500
    finally:
        cursor.close()
        connection.close()



'''
@bp.route('/img/<path:filename>')
def flask_img(filename):
    return current_app.send_static_file("img/"+filename)
'''


def parsToJson(query_result, columns):
    resp = []
    for row in query_result:
        obj = {}
        for i in range(len(columns)):
            obj[columns[i]] =row[columns[i]] if type(row[columns[i]]) != datetime.date else row[columns[i]].strftime('%Y-%m-%d')
        resp.append(obj)
    return resp


@bp.route('/login', methods=['GET'])
def login():
    email = request.args.get('email')
    password = request.args.get('password')
    
    connection = db.getdb()
    resp = {}
    try:
        cursor = connection.cursor(dictionary=True)
        cursor.execute("SELECT * FROM utente WHERE email = %s AND password = %s", (email, password))
        query_result = cursor.fetchone()
        if query_result:
            resp = {'message': 'Login successful', 'user': parsToJson([query_result], cursor.column_names)[0]}
        else:
            resp = {'message': 'Invalid email or password'}
    finally:
        cursor.close()
    return jsonify(resp)


@bp.route('/liste/verifica', methods=['GET'])
def wishlist_checking():
    RefUtenteLista = request.args.get('RefUtenteLista')
    RefProdottoLista = request.args.get('RefProdottoLista')
    
    connection = db.getdb()
    resp = {}
    try:
        cursor = connection.cursor(dictionary=True)
        cursor.execute("SELECT * FROM lista WHERE RefUtenteLista = %s AND RefProdottoLista = %s", (RefUtenteLista, RefProdottoLista))
        query_result = cursor.fetchone()
        if query_result:
            resp = {'message': 'Item alredy listed', 'wishlist': parsToJson([query_result], cursor.column_names)[0]}
        else:
            resp = {'message': 'Item not listed'}
    finally:
        cursor.close()
    return jsonify(resp)

@bp.route('/liste', methods=['POST'])
def add_to_wishlist():
    data = request.get_json()
    email = data.get('email')
    id_prodotto = data.get('idProdotto')

    if not email or not id_prodotto:
        return jsonify({"error": "Email e idProdotto sono campi obbligatori"}), 400

    connection = db.getdb()
    try:
        cursor = connection.cursor()
        # Verifica se il prodotto è già nella lista dei desideri
        cursor.execute("SELECT 1 FROM lista WHERE RefUtenteLista = %s AND RefProdottoLista = %s", (email, id_prodotto))
        exists = cursor.fetchone()
        if exists:
            return jsonify({"message": "Prodotto già nella lista dei desideri"}), 400

        # Inserisci il prodotto nella lista dei desideri
        cursor.execute("INSERT INTO lista (RefUtenteLista, RefProdottoLista) VALUES (%s, %s)", (email, id_prodotto))
        connection.commit()

        return jsonify({"message": "Prodotto aggiunto alla lista dei desideri"}), 201
    except Exception as e:
        connection.rollback()
        return jsonify({"error": f"Errore durante l'aggiunta alla lista dei desideri: {str(e)}"}), 500
    finally:
        cursor.close()
        connection.close()
        


@bp.route('/liste', methods=['GET'])
def get_wishlist():
    email = request.args.get('email')

    if not email:
        return jsonify({"error": "Email è un campo obbligatorio"}), 400

    connection = db.getdb()
    try:
        cursor = connection.cursor()
        cursor.execute("""
            SELECT p.idProdotto, p.name, p.price, p.starImage, p.resource, p.nPezzi, p.categoria, p.description
            FROM prodotto p 
            INNER JOIN lista l ON p.idProdotto = l.RefProdottoLista 
            WHERE l.RefUtenteLista = %s
        """, (email,))
        results = cursor.fetchall()

        # Convert results to list of dictionaries
        item_list = []
        for row in results:
            item = {
                "idProdotto": row[0],
                "name": row[1],
                "price": row[2],
                "starImage": row[3],
                "resource": row[4],
                "nPezzi" : row[5],
                "categoria" : row[6],
                "description" : row[7]
            }
            item_list.append(item)

        return jsonify(item_list), 200
    except Exception as e:
        return jsonify({"error": f"Errore durante il recupero della lista dei desideri: {str(e)}"}), 500
    finally:
        cursor.close()
        connection.close()

    

@bp.route('/liste/<string:email>/<int:idProdotto>', methods=['DELETE'])
def delete_wishlist_item(email, idProdotto):
    connection = db.getdb()
    try:
        cursor = connection.cursor()
        cursor.execute("DELETE FROM lista WHERE RefUtenteLista = %s AND RefProdottoLista = %s", (email, idProdotto))
        connection.commit()
        resp = {'message': 'Product deleted successfully from wishlist'}
    except db.IntegrityError:
        resp = {'error': 'Error deleting product from wishlist'}
    finally:
        cursor.close()
    return jsonify(resp)

@bp.route('/notifiche', methods=['GET'])
def get_notifiche():
    RefUtenteNotifica = request.args.get('RefUtenteNotifica')
    connection = db.getdb()
    result = []
    try:
        cursor = connection.cursor(dictionary=True)
        cursor.execute("SELECT idNotifica, RefUtenteNotifica, messaggio, dataInvio FROM notifica WHERE RefUtenteNotifica = %s order by dataInvio desc", (RefUtenteNotifica, ))
        query_result = cursor.fetchall()
        if query_result:
            result = parsToJson(query_result, cursor.column_names)
    except mysql.connector.Error as e:
        app.logger.error(f"Database error: {str(e)}")
    finally:
        cursor.close()
        connection.close()
    return jsonify(result)


@bp.route('/notifiche/registrazione', methods=['POST'])
def create_notifica():
    email = request.json.get('email')
    
    connection = db.getdb()
    try:
        cursor = connection.cursor(dictionary=True)

        # Ottieni il nome e il cognome dell'utente
        cursor.execute("SELECT nome, cognome FROM utente WHERE email = %s", (email,))
        utente = cursor.fetchone()  # Consuma qualsiasi risultato rimanente
        
        if utente is None:
            return jsonify({'errore': 'Utente non trovato'}), 404

        nome = utente['nome']
        cognome = utente['cognome']
        messaggio = f"Benvenuto su Umami, {nome} {cognome}"

        # Inserisci la notifica con la data attuale ottenuta dal database
        cursor.execute(
            "INSERT INTO notifica (RefUtenteNotifica, messaggio, dataInvio) VALUES (%s, %s, CURDATE())",
            (email, messaggio)
        )
        connection.commit()
        idNotifica = cursor.lastrowid

        # Recupera la data inserita per la notifica
        cursor.execute("SELECT dataInvio FROM notifica WHERE id = %s", (idNotifica,))
        data_attuale = cursor.fetchone()['dataInvio']

        resp = {
            'id': idNotifica,
            'email': email,
            'messaggio': messaggio,
            'data': data_attuale
        }
        return jsonify(resp)
    except Exception as e:
        return jsonify({'errore': str(e)}), 500
    finally:
        if cursor:
            try:
                cursor.close()
            except Exception as e:
                logging.error("Errore durante la chiusura del cursore: %s", str(e))
        if connection:
            try:
                connection.close()
            except Exception as e:
                logging.error("Errore durante la chiusura della connessione: %s", str(e))


@bp.route('/foto', methods=['GET'])
def get_foto():
    RefProdotto = request.args.get('RefProdotto')
    connection = db.getdb()
    result = []
    try:
        cursor = connection.cursor(dictionary=True)
        cursor.execute("SELECT RefProdottoFoto, foto FROM foto_prodotto WHERE RefProdottoFoto = %s", (RefProdotto, ))
        query_result = cursor.fetchall()
        if query_result:
            result = parsToJson(query_result, cursor.column_names)
    except Exception as e:
        return jsonify({"error": f"Errore durante il recupero della lista dei desideri: {str(e)}"}), 500
    finally:
        cursor.close()
        connection.close()
    return jsonify(result)