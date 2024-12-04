import React, { useEffect, useState } from "react";

const SimLog: React.FC = () => {
    const [messages, setMessages] = useState<string[]>([]);

    useEffect(() => {
        // const socket = new WebSocket("ws://localhost:8080/ws");
        const socket = new WebSocket(
            "wss://ticketing---system-32a1f2f59169.herokuapp.com/ws"
        );

        socket.onopen = () => {
            console.log("WebSocket connection established");
        };

        socket.onmessage = (event) => {
            const newMessage = event.data;
            setMessages((prevMessages) => [...prevMessages, newMessage]);
        };

        socket.onerror = (error) => {
            console.error("WebSocket error:", error);
        };

        socket.onclose = () => {
            console.log("WebSocket connection closed");
        };

        return () => {
            socket.close();
        };
    }, []);

    return (
        <div>
            <h2>WebSocket Messages</h2>
            <ul>
                {messages.map((message, index) => (
                    <li key={index}>{message}</li>
                ))}
            </ul>
        </div>
    );
};

export default SimLog;
