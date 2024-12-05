import React, { useState } from "react";
import API from "../../axios.tsx";
import TextField from "@mui/material/TextField";

interface SimInputFormProps {
    eventId: string;
}

const SimInputForm: React.FC<SimInputFormProps> = ({ eventId }) => {
    const [inputs, setInputs] = useState<number[]>(Array(5).fill(""));
    const qNames = [
        "vendor release rate",
        "customer retrieval rate",
        "no of vendors",
        "no of customers",
        "simulation speed (ms)",
    ];

    const handleChange = (index: number, value: string) => {
        const newInputs = [...inputs];
        newInputs[index] = parseInt(value, 10);
        setInputs(newInputs);
    };

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        console.log("Sending data:", inputs); // Log the data being sent
        try {
            const response = await API.post(
                `/event/${eventId}/startSim`,
                inputs // Send the array directly
            );
            console.log("Response:", response.data);
        } catch (error) {
            if (error.response && error.response.status === 409) {
                alert("The simulation is already running");
            } else {
                console.error("Error:", error);
            }
        }
    };

    const handleStopSim = async () => {
        try {
            const response = await API.post(`/event/${eventId}/stopSim`);
            console.log("Response:", response.data);
        } catch (error) {
            if (error.response && error.response.status === 409) {
                alert("The simulation is not running");
            } else {
                console.error("Error:", error);
            }
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            {inputs.map((input, index) => (
                <div key={index}>
                    <label>
                        <TextField
                            id="outlined-basic"
                            label={qNames[index]}
                            variant="outlined"
                            type="number"
                            value={input}
                            onChange={(e) =>
                                handleChange(index, e.target.value)
                            }
                        />
                    </label>
                </div>
            ))}
            <button type="submit">Start Sim</button>
            <button type="button" onClick={handleStopSim}>Stop Sim</button>
        </form>
    );
};

export default SimInputForm;