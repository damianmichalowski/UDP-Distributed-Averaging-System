## **UDP Distributed Averaging System**

### **Overview**
The UDP Distributed Averaging System is a network-based application designed to compute the average of values received from multiple clients (Slaves) and broadcast the computed average to all devices in the local network. The system uses the **UDP protocol**, which is connectionless and lightweight, making it ideal for fast, real-time data transmission.

### **Key Components**

#### **Master**:
- The Master listens for incoming data from Slave clients on a specific port (e.g., `<port>`).
- It calculates the average of the received values.
- Once the average is calculated, the Master broadcasts the result to all devices in the local network via UDP on a dedicated port (e.g., `60000`).

#### **Slave**:
- Each Slave sends a numerical value to the Master on the listening port (`<port>`).
- Slaves can also send specific commands, such as requesting the average calculation or signaling the termination of the Master’s operation.

### **Communication Protocol**
- **UDP Protocol**: The system uses **User Datagram Protocol (UDP)**, which is a connectionless protocol that does not guarantee message delivery, but offers faster communication compared to TCP.

#### **Message Format**:
- Messages are sent as plain text (strings), which are converted to byte arrays for UDP transmission.
- Examples of message content:
    - **Numerical values** (e.g., `10`, `5`, `20`) sent by Slave to Master.
    - **Special commands**:
        - `0`: Request for average calculation.
        - `-1`: Command to terminate the Master’s process.
    - **Computed average**: Sent by Master, e.g., `11` (rounded down to the nearest integer).

#### **Broadcasting**:
- The Master calculates the broadcast address for the local network and sends the computed average to port e.g.`60000` on all devices in the same subnet.
- **Broadcast Address**: The broadcast address (e.g., `255.255.255.255`) is dynamically calculated by the Master and displayed on the console for reference.

---

### **Flow of Communication**

1. **Slave to Master**:
    - Slaves send data to the Master on the specified port (e.g., `<port>`), such as a numerical value like `10` or a request for the average.

2. **Master to Broadcast**:
    - After processing the data, the Master computes the average and sends the result to the broadcast address on port `60000` to all devices in the local network.

3. **End of Communication**:
    - Slaves can send a special `-1` message to the Master, indicating that it should terminate.

---

### **Network Configuration**

- **IP Address**: The system uses the local IP address to identify devices on the local network.
- **Port `<port>`**: The port on which the Master listens for incoming data from Slaves.
- **Port e.g.`60000`**: The port used by the Master to broadcast the calculated average to all devices in the local network.

---

### **Implementation Considerations**

- **UDP Protocol**: While UDP ensures fast transmission, it does not guarantee packet delivery. In case of network congestion or issues, some messages might be lost.
- **Broadcasting**: The system uses a local broadcast address (e.g., `255.255.255.255`) to ensure that all devices within the same subnet can receive the broadcasted average.
- **Termination**: The Master waits for a `-1` message to shut down gracefully.

---

### **Testing and Usage**

#### **Testing with Batch or Shell Files**

To test the system, you can use batch files on **Windows** or shell scripts on **Linux/macOS** to automate the running of multiple Slave instances and a Master. The typical testing procedure involves the following steps:

1. **Running the Master**:
    - Use the following command to run the Master, where `<port>` is the port the Master listens on:
      ```bash
      java DAS <port> <number>
      ```
    - This starts the Master on the specified port and listens for incoming data from Slaves.

2. **Running the Slave**:
    - After the Master is running, you can run a Slave using the same port, e.g., `10000`:
      ```bash
      java DAS <port> <number>
      ```
    - If the port is already in use by the Master, the Slave will automatically enter **Slave mode**, sending data to the Master on the specified port.

#### **Example of `test1.bat` or `test1.sh`**:

**Windows Batch File (test1.bat)**:
```bat
start java DAS 10000 263
timeout /t 1
start java DAS 10000 264
timeout /t 1
java DAS 10000 2635
timeout /t 1
start java DAS 10000 2635
```

**Linux/Mac Shell Script (test1.sh)**:
```bash
#!/bin/bash

# Start Master in the background
echo "Starting Master..."
java DAS 10000 263 &
sleep 1

# Start Slave 1 in the background
echo "Starting Slave 1..."
java DAS 10000 264 &
sleep 1

# Start Slave 2
echo "Starting Slave 2..."
java DAS 10000 2635
sleep 1

# Start Slave 3 in the background
echo "Starting Slave 3..."
java DAS 10000 2635 &
```

In both cases, the script starts the Master, waits briefly (using `timeout` or `sleep`), then starts multiple Slaves. Each Slave will send a value to the Master, and the Master will compute and broadcast the average.

---

### **Key Points**
- The system uses **UDP** for communication, which is fast but does not guarantee packet delivery.
- The **Master** listens on the specified port (`<port>`) for incoming data from Slaves.
- The **Slave** sends data to the Master on the same port, and once the Master finishes computing the average, it broadcasts the result to all devices on port e.g.`60000`.
- Testing is performed using batch files (`.bat`) or shell scripts (`.sh`), which automate running multiple Slave instances along with the Master.