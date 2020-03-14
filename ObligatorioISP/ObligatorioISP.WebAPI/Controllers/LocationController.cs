﻿using Microsoft.AspNetCore.Mvc;
using ObligatorioISP.Services.Contracts;

namespace ObligatorioISP.WebAPI.Controllers
{
    [Route("api/[controller]")]
    public class LocationController : Controller
    {
        IProximityNotificationService service;
        public LocationController(IProximityNotificationService notificationService)
        {
            service = notificationService;
        }

        [HttpPost("update/{token}")]
        public IActionResult UpdateLocation(string token, [FromQuery] double lat, [FromQuery] double lng)
        {
            //Notification will be sent by firebase.
            service.NotifyIfCloseToLandmark(token, lat, lng);
            //Wether it is sent or not in the end, we return OK. It is not urgent that the notification is sent.
            return Ok(new { Message = "Updated successfully" });
        }
    }
}
