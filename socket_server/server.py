from socket import *
import sys,threading,time,pymysql,json

def tcplink(sock,id):
    while True:
        try:
            command = sock.recv(1024).decode("utf-8")
            print(command)
            clients[id][1] = 600
            clients[id][2] = 1
            if not command:
                clients[id][2] = 0
                clients[id][0].shutdown(2)
                clients[id][0].close()
                clients[id][0] = None
                break
            if command == "add":
                data = sock.recv(1024).decode("utf-8")
                print(data)
                body = json.loads(data)
                sock.send("add".encode("utf-8"))
                time.sleep(0.2)
                sock.send(sqlInsertOrUpdateOrDelete("insert into bodydata(userid,height,weight,muscle) value("+id+","+body["height"]+","+body["weight"]+","+body["muscle"]+")").encode("utf-8"))
            # if command == "" here response the request use send(str.encode()) to send response to client
        except:
            clients[id][2] = 1
            clients[id][0].shutdown(2)
            clients[id][0].close()
            clients[id][0] = None
            break


def sqlSelect(sql):
    db = pymysql.connect("localhost","public","abc123abc123","socket")
    cursor = db.cursor()
    try:
        cursor.execute(sql)
        results = cursor.fetchall()
        db.close()
        return results
    except:
        db.close()
        return "something wrong!"

def sqlInsertOrUpdateOrDelete(sql):
    db = pymysql.connect("localhost","public","abc123abc123","socket")
    cursor = db.cursor()
    try:
        cursor.execute(sql)
        db.commit()
        db.close()
        return "True"
    except:
        db.rollback()
        db.close()
        return "False"

def clock():
    while True:
        for client in clients.keys():
            if clients[client][2] == 1:
                clients[client][1] -= 1
                if clients[client][1] < 0:
                    clients[client][2] = 0
                    clients[client][0].shutdown(2)
                    clients[client][0].close()
                    clients[client][0] = None
        time.sleep(1)

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python server.py [listen IP] [listen PORT]")
        sys.exit()

    clients = dict() #thread pool

    t = threading.Thread(target=clock)
    t.start()

    addr = (sys.argv[1],int(sys.argv[2]))
    tcp = socket(AF_INET,SOCK_STREAM)
    tcp.bind(addr)

    tcp.listen(100)

    while True:
        client, address = tcp.accept()

        type = client.recv(1024).decode("utf-8")
        if type == "login":
            username = client.recv(1024).decode("utf-8")
            password = client.recv(1024).decode("utf-8")

            infor = sqlSelect("select id,password from userinfo where username = '"+username+"';")
            for row in infor:
                if row[1] == password:
                    t = threading.Thread(target=tcplink,args=(client,str(row[0])))
                    t.start()
                    client.send("1".encode("utf-8"))
                    clients[str(row[0])] = [client,600,1]
                else:
                    client.send("0".encode("utf-8"))
                    client.shutdown(2)
                    client.close()
        elif type == "sign up":
            username = client.recv(1024).decode("utf-8")
            password = client.recv(1024).decode("utf-8")
            sex = client.recv(1024).decode("utf-8")
            client.send(sqlInsertOrUpdateOrDelete("insert into userinfo(username,password,sex) value('"+username+"','"+password+"',"+sex+")").encode("utf-8"))
            client.shutdown(2)
            client.close()
            


