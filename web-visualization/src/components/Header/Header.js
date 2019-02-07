import React from 'react';
import Select from 'react-select';
import bookCoverSrc from '../../assets/book-cover.png';

const Header = ({ vizOptions, selectedVizOption, vizOptionChangeHandler, vizButtonClickHandler }) => {
    return (
        <div className="Header">
            <div className="Header__left">
                <a href="https://www.amazon.com/Trapped-Wing-Hall-Yourself-Goosebumps/dp/0590566466" title="See the book on Amazon">
                    <img src={bookCoverSrc} alt="Goosebumps Book Cover" />
                </a>
                <div className="Header__left__text">
                    <h1>Visualizing paths through <i>Goosebumps: Trapped in Bat Wing Hall</i></h1>
                    <h2>Created by <a href="https://github.com/mileshenrichs">Miles Henrichs</a></h2>
                </div>
            </div>

            <div className="Header__right">
                <div className="Select-container">
                    <Select
                        className="Select"
                        value={selectedVizOption}
                        onChange={(option) => vizOptionChangeHandler(option)}
                        options={vizOptions}
                        isSearchable={false}
                    />
                </div>
                <button className="change-btn" onClick={vizButtonClickHandler}>
                    <span>Change</span>
                </button>
            </div>
        </div>
    );
};

export default Header;
