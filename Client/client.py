from socket import *
import os,hashlib,time,threading

def get():
    while True:
        command = tcp.recv(1024).decode("utf-8")
        if not command:
            tcp.shutdown(2)
            tcp.close()
        if command == "add":
            res = tcp.recv(1024).decode("utf-8")
            if res == "True":
                print("upload new data succeed!")
            else:
                print("upload new data failed!")
def send():
    while True:
        command = input(">>")
        if command == "add":
            height = input("height:")
            weight = input("weight:")
            muscle = input("muscle:")
            data = '{"height":'+height+',"weight":'+weight+',"muscle":'+muscle+'}'
            tcp.send("add".encode("utf-8"))
            time.sleep(0.2)
            tcp.send(data.encode("utf-8"))

def login():
    if(os.path.exists("log")):
        f = open("log","r")
        username = f.readline().replace("\n","")
        password = f.readline()
        tcp.send("login".encode("utf-8"))
        time.sleep(0.2)
        tcp.send(username.encode("utf-8"))
        time.sleep(0.2)
        tcp.send(password.encode("utf-8"))
        response = tcp.recv(1024).decode("utf-8")
        if response == "0":
            tcp.shutdown(2)
            tcp.close()
        else:
            t = threading.Thread(target=get)
            t.start()
            t = threading.Thread(target=send)
            t.start()
    else:
        s = hashlib.sha256()
        s.update(input("username:").encode())
        username = s.hexdigest()
        s.update(input("password:").encode())
        password = s.hexdigest()
        sex = input("Sex (1 boy/ 0 girl):")

        tcp.send("sign up".encode("utf-8"))
        time.sleep(0.2)
        tcp.send(username.encode("utf-8"))
        time.sleep(0.2)
        tcp.send(password.encode("utf-8"))
        time.sleep(0.2)
        tcp.send(sex.encode("utf-8"))

        if tcp.recv(1024).decode("utf-8") == "True":
            f = open("log","w")
            f.write(username+"\n")
            f.write(password)
            f.close()
            tcp.shutdown(2)
            tcp.close()
            login(tcp)

if __name__ == "__main__":
    addr = ("140.143.126.73",2020)
    tcp = socket(AF_INET,SOCK_STREAM)
    tcp.connect(addr)
    login()