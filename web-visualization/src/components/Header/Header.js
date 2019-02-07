import React from 'react';
import Select from 'react-select';
import bookCoverSrc from '../../assets/book-cover.png';

const Header = ({ vizOptions, selectedVizOption, vizOptionChangeHandler, vizButtonClickHandler }) => {
    return (
        <div className="Header">
            <div className="Header__left">
                <img src={bookCoverSrc} alt="Goosebumps Book Cover" />
                <div className="Header__left__text">
                    <h1>Visualizing paths through <i>Goosebumps: Escape from Bat Wing Hall</i></h1>
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
                    />
                </div>
                <button className="see-it" onClick={vizButtonClickHandler}>
                    <span>SEE IT</span>
                </button>
            </div>
        </div>
    );
};

export default Header;
