import React, { useState, useEffect } from "react";
import API from "../../axios.tsx";
import TextField from "@mui/material/TextField";
import axios from "axios";
import Button from "@mui/material/Button";

interface SimInputFormProps {
    eventId: string;
    onReload: () => void;
}

/**
 * SimInputForm component for configuring and starting/stopping a simulation.
 * Fetches initial configuration and allows user to update and submit new configuration.
 * @param eventId - The ID of the event for which the simulation is being configured.
 * @param onReload - Callback function to reload the simulation data.
 */
const SimInputForm: React.FC<SimInputFormProps> = ({ eventId, onReload }) => {
    // State to store the input values for the simulation configuration
    const [inputs, setInputs] = useState<number[]>(Array(6).fill(0));
    // State to store error messages
    const [errorMessage, setErrorMessage] = useState<string | null>(null);
    // Array of labels for the input fields
    const qNames = [
        "vendor release rate",
        "customer retrieval rate",
        "no of vendors",
        "no of customers",
        "no of VIP customers",
        "simulation speed (ms)",
    ];

    /**
     * Fetches the initial configuration for the event.
     */
    useEffect(() => {
        const fetchConfig = async () => {
            try {
                const response = await API.get(`/event/${eventId}/getConfig`);
                setInputs(response.data);
            } catch (error) {
                console.error("Error fetching config:", error);
            }
        };
        fetchConfig();
    }, [eventId]);

    /**
     * Handles changes to the input fields.
     * @param index - The index of the input field being changed.
     * @param value - The new value of the input field.
     */
    const handleChange = (index: number, value: string) => {
        const newInputs = [...inputs];
        newInputs[index] = parseInt(value, 10);
        setInputs(newInputs);
    };

    /**
     * Handles the form submission to start the simulation.
     * @param event - The form submission event.
     */
    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        if (inputs.some((input) => input < 0)) {
            setErrorMessage("Cannot enter negative numbers");
            return;
        }
        setErrorMessage(null);
        try {
            await API.post(`/event/${eventId}/startSim`, inputs);
            onReload();
        } catch (error: unknown) {
            if (axios.isAxiosError(error) && error.response) {
                if (error.response.status === 409) {
                    alert("The simulation is already running");
                } else {
                    console.error("Error:", error);
                }
            }
        }
    };

    /**
     * Handles stopping the simulation.
     */
    const handleStopSim = async () => {
        try {
            await API.post(`/event/${eventId}/stopSim`);
        } catch (error: unknown) {
            if (axios.isAxiosError(error) && error.response) {
                if (error.response.status === 409) {
                    alert(
                        error.response.data === "Simulation is not running."
                            ? "The simulation is not running"
                            : "Event not found"
                    );
                } else {
                    console.error("Error:", error);
                }
            }
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>Simulation Configuration</h2>
            {qNames.map((label, index) => (
                <div key={index} style={{ marginBottom: 20 }}>
                    <TextField
                        label={label}
                        variant="outlined"
                        type="number"
                        value={inputs[index]}
                        onChange={(e) => handleChange(index, e.target.value)}
                    />
                </div>
            ))}
            {errorMessage && (
                <div style={{ marginBottom: 10, color: "red" }}>
                    {errorMessage}
                </div>
            )}
            <div>
                <Button
                    variant="outlined"
                    type="submit"
                    sx={{ marginRight: 2 }}
                >
                    Start Sim
                </Button>
                <Button
                    variant="outlined"
                    type="button"
                    onClick={handleStopSim}
                >
                    Stop Sim
                </Button>
            </div>
        </form>
    );
};

export default SimInputForm;