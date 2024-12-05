import React, { useContext, useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { UserContext } from "../Common/UserContext";
import API from "../../axios";
import Button from "@mui/material/Button";

interface Event {
    eventId: string;
    name: string;
    ownerId: string;
    desc: string;
    totalTickets: number;
    maxCapacity: number;
    currentTickets: number;
    issuedTickets: number;
    totalTicketsAdded: number;
    vendors: string[];
}

interface Vendor {
    vendorId: string;
    username: string;
}

const AddVendors: React.FC = () => {
    const { eventId } = useParams<{ eventId: string }>();
    const userContext = useContext(UserContext);
    const navigate = useNavigate();
    const [event, setEvent] = useState<Event | null>(null);
    const [vendorDetails, setVendorDetails] = useState<{
        [key: string]: Vendor;
    }>({});
    const [allVendors, setAllVendors] = useState<Vendor[]>([]);
    const [selectedVendors, setSelectedVendors] = useState<string[]>([]);

    useEffect(() => {
        const fetchEvent = async () => {
            if (userContext?.userData) {
                const url = `/event/${eventId}`;
                try {
                    const response = await API.get(url, {
                        headers: {
                            "Content-Type": "application/json",
                        },
                    });
                    setEvent(response.data);
                    setSelectedVendors(response.data.vendors); // Initialize selectedVendors with event vendors
                } catch (error) {
                    console.error("Error fetching event:", error);
                }
            }
        };

        fetchEvent();
    }, [eventId, userContext]);

    useEffect(() => {
        if (event) {
            event.vendors.forEach((vendorId: string) => {
                if (!vendorDetails[vendorId]) {
                    fetchVendorDetails(vendorId);
                }
            });
        }
    }, [event, vendorDetails]);

    const fetchVendorDetails = async (vendorId: string) => {
        try {
            const response = await API.get(`/vendor/${vendorId}`, {
                headers: {
                    "Content-Type": "application/json",
                },
            });
            setVendorDetails((prevDetails) => ({
                ...prevDetails,
                [vendorId]: response.data,
            }));
        } catch (error) {
            console.error(`Error fetching vendor ${vendorId}:`, error);
        }
    };

    useEffect(() => {
        const fetchAllVendors = async () => {
            try {
                const response = await API.get("/vendors", {
                    headers: {
                        "Content-Type": "application/json",
                    },
                });
                setAllVendors(response.data);
            } catch (error) {
                console.error("Error fetching all vendors:", error);
            }
        };

        fetchAllVendors();
    }, []);

    const handleCheckboxChange = (vendorId: string) => {
        setSelectedVendors((prevSelected) =>
            prevSelected.includes(vendorId)
                ? prevSelected.filter((id) => id !== vendorId)
                : [...prevSelected, vendorId]
        );
    };

    const handleChangeVendors = async () => {
        try {
            const response = await API.put(
                `/event/${eventId}/vendors`,
                selectedVendors,
                {
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            );
            console.log("Response:", response.data);
            alert("Vendors updated successfully");
            navigate(`/event/${eventId}`);
        } catch (error) {
            console.error("Error changing vendors:", error);
        }
    };

    if (!event) {
        return <p>Loading...</p>;
    }

    return (
        <div>
            <h2 style={{margin : 50}}>Change Vendors</h2>
            <h3 className={"user-details"}>Current Vendors</h3>
            {event.vendors.map((vendorId) => (
                <p key={vendorId} className={"user-details"}>
                    {vendorDetails[vendorId]?.username || "Loading..."}
                </p>
            ))}
            <h3 className={"user-details"} style={{marginTop : 30}}>All Vendors</h3>
            <form>
                {allVendors.map(
                    (vendor) =>
                        vendor.vendorId !== event.ownerId && (
                            <div key={vendor.vendorId} className={"user-details"}>
                                <label >
                                    <input
                                        type="checkbox"
                                        id={vendor.vendorId}
                                        value={vendor.vendorId}
                                        checked={selectedVendors.includes(
                                            vendor.vendorId
                                        )}
                                        onChange={() =>
                                            handleCheckboxChange(
                                                vendor.vendorId
                                            )
                                        }
                                    />
                                    {vendor.username}
                                </label>
                            </div>
                        )
                )}
                <div className={"buttons"}>
                    <Button type="button" variant="outlined" onClick={handleChangeVendors}>
                        Change Vendors
                    </Button>
                </div>
            </form>
        </div>
    );
};

export default AddVendors;
