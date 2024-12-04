import React, { createContext, useState, ReactNode } from "react";

interface UserContextType {
    userData: {
        username: string;
        userId: string;
        isVendor: boolean;
    } | null;
    setUserData: (data: {
        username: string;
        userId: string;
        isVendor: boolean;
    }) => void;
}

export const UserContext = createContext<UserContextType | undefined>(
    undefined
);

export const UserProvider: React.FC<{ children: ReactNode }> = ({
    children,
}) => {
    const [userData, setUserData] = useState<UserContextType["userData"]>(null);

    return (
        <UserContext.Provider value={{ userData, setUserData }}>
            {children}
        </UserContext.Provider>
    );
};
