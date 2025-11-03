import bluetooth
import time
import threading

server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
port = bluetooth.PORT_ANY
server_sock.bind(("", port))
server_sock.listen(1)

uuid = "00001101-0000-1000-8000-00805F9B34FB"

bluetooth.advertise_service(
    server_sock,
    "MorseServer",
    service_id=uuid,
    service_classes=[uuid, bluetooth.SERIAL_PORT_CLASS],
    profiles=[bluetooth.SERIAL_PORT_PROFILE]
)

print("Waiting for connection...")

client_sock, client_info = server_sock.accept()
print(f"Connected: {client_info}")

def blink_dot():
    print("●", end="", flush=True)
    time.sleep(0.2)
    print("\b ", end="", flush=True)
    time.sleep(0.2)

def blink_dash():
    print("━━━", end="", flush=True)
    time.sleep(0.6)
    print("\b   ", end="", flush=True)
    time.sleep(0.2)

def morse_blink(code):
    print(f"\n[СВЕТОДИОД] Морзе: {code}")
    print("           ", end="")
    for symbol in code:
        if symbol == '.':
            blink_dot()
        elif symbol == '-':
            blink_dash()
        elif symbol == ' ':
            time.sleep(0.6)
        elif symbol == '/':
            time.sleep(1.4)
        else:
            time.sleep(0.2)
    print("\n[ГОТОВО]\n")


try:
    while True:
        data = client_sock.recv(1024)
        if not data:
            break
        
        message = data.decode().strip()
        print("Received:", message)
        
        threading.Thread(target=morse_blink, args=(message,), daemon=True).start()

except Exception as e:
    print("Error:", e)
finally:
    print("Connection closed")
    client_sock.close()
    server_sock.close()