import React, { useEffect, useState, useContext } from "react";
import { UserContext } from "../Common/UserContext";
import { LineChart } from "@mui/x-charts/LineChart";

const SimProgress: React.FC = () => {
    const userContext = useContext(UserContext);
    const [currentTickets, setCurrentTickets] = useState<number[]>([]);
    const [allSoldTickets, setAllSoldTickets] = useState<number[]>([]);
    const [xLabels, setXLabels] = useState<string[]>([]);

    useEffect(() => {
        if (userContext?.userData) {
            const socket = new WebSocket(`ws://localhost:8080/ws/integers`);
            // const socket = new WebSocket(
            //     `wss://ticketing---system-32a1f2f59169.herokuapp.com/ws/integers`
            // );

            socket.onopen = () => {
                console.log("WebSocket connection established");
            };

            socket.onmessage = (event) => {
                const message = event.data;
                const parts = message.split(",").map(Number);
                const half = Math.ceil(parts.length / 2);
                const newCurrentTickets = parts.slice(0, half);
                const newAllSoldTickets = parts.slice(half);

                setCurrentTickets((prevTickets) => [
                    ...prevTickets,
                    ...newCurrentTickets,
                ]);
                setAllSoldTickets((prevTickets) => [
                    ...prevTickets,
                    ...newAllSoldTickets,
                ]);
                setXLabels((prevLabels) => [
                    ...prevLabels,
                    `Update ${prevLabels.length + 1}`,
                ]);
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
        }
    }, [userContext]);

    return (
        <div>
            <LineChart
                width={1000}
                height={500}
                series={[
                    { data: allSoldTickets, label: "All Sold Tickets" },
                    { data: currentTickets, label: "Current Tickets" },
                ]}
                xAxis={[{ scaleType: "point", data: xLabels }]}
            />
        </div>
    );
};

export default SimProgress;
