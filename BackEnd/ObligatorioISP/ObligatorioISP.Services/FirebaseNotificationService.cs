using Microsoft.Extensions.Configuration;
using Newtonsoft.Json;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.Services.Contracts;
using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;

namespace ObligatorioISP.Services
{
    public class FirebaseNotificationService : IProximityNotificationService
    {
        private ILandmarksRepository landmarks;
        private string applicationID;
        private string senderID;

        public FirebaseNotificationService(IConfiguration configuration, ILandmarksRepository landmarksStorage)
        {
            landmarks = landmarksStorage;
            applicationID = configuration["Firebase:ApplicationID"];
            senderID = configuration["Firebase:SenderID"];

        }

    }
}
