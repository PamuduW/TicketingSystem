import React, { createContext, useState, ReactNode } from "react";

interface UserData {
    username: string;
    userId: string;
    isVendor: boolean;
}

interface UserContextType {
    userData: UserData | null;
    setUserData: (data: UserData) => void;
}

export const UserContext = createContext<UserContextType | undefined>(
    undefined
);

export const UserProvider: React.FC<{ children: ReactNode }> = ({
    children,
}) => {
    const [userData, setUserData] = useState<UserData | null>(null);

    return (
        <UserContext.Provider value={{ userData, setUserData }}>
            {children}
        </UserContext.Provider>
    );
};
