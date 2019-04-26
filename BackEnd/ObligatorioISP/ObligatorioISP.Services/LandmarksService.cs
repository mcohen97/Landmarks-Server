using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.Services.Contracts;
using System;
using System.Collections.Generic;
using System.Text;

namespace ObligatorioISP.Services
{
    public class LandmarksService:ILandmarksService
    {
        private ILandmarksRepository landmarks;

        public LandmarksService(ILandmarksRepository landmarksStorage) {
            landmarks = landmarksStorage;
        }
    }
}
