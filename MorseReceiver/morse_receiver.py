import bluetooth

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

try:
    while True:
        data = client_sock.recv(1024)
        if not data:
            break
        print("Received:", data.decode().strip())
except Exception as e:
    print("Error:", e)
finally:
    print("Connection closed")
    client_sock.close()
    server_sock.close()
