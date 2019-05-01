using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using ObligatorioISP.DataAccess;
using ObligatorioISP.DataAccess.Contracts;
using ObligatorioISP.Services;
using ObligatorioISP.Services.Contracts;

namespace ObligatorioISP.WebAPI
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddMvc().SetCompatibilityVersion(CompatibilityVersion.Version_2_1);

            services.AddScoped<ILandmarksRepository>(provider=> new SqlServerLandmarksRepository(
                new SqlServerConnectionManager(Configuration.GetConnectionString("Landmarks")),
                GetMediaPath("Images","Uri"),
                GetMediaPath("Audios", "Uri")));

            services.AddScoped<IToursRepository>(provider => new SqlServerToursRepository(
                new SqlServerConnectionManager(Configuration.GetConnectionString("Landmarks")),
                new SqlServerLandmarksRepository(
                new SqlServerConnectionManager(Configuration.GetConnectionString("Landmarks")),
                GetMediaPath("Images", "Uri"),
                GetMediaPath("Audios", "Uri"))
                ));

            services.AddScoped<IImagesRepository, DiskImagesRepository>();
            services.AddScoped<IAudiosRepository, DiskAudiosRepository>();

            services.AddScoped<ILandmarksService, LandmarksService>();
            services.AddScoped<IToursService, ToursService>();
        }

        private string GetMediaPath(string section, string key)
        {
            return Configuration.GetSection(section).GetValue<string>(key);
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }
            else
            {
                app.UseHsts();
            }

            app.UseHttpsRedirection();
            app.UseMvc();
        }
    }
}
